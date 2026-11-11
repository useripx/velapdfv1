package com.njagakneai.velapdf.metopen.domain.ontology

import android.content.Context
import com.njagakneai.velapdf.metopen.domain.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

/**
 * METOPEN TA 2026/2027 - PROYEK VELAPDF
 * Mahasiswa: Yogi Ario Pratama (NPM: 2313020004)
 * 
 * Kelas Pemroses dan Pemuat Ontologi Domain Dokumen Umum (JSON)
 * Bertugas memuat skema representasi pengetahuan dokumen formal/regulasi
 * dan memetakan kalimat hasil prapemrosesan NLP menjadi Fakta Semantik terstruktur (SPO).
 */
class OntologyLoader(
    private val context: Context
) {

    private var cachedTaxonomy: DocumentOntologyTaxonomy? = null

    /**
     * Membaca dan mengurai berkas JSON Ontologi Dokumen Umum dari folder assets lokal.
     */
    suspend fun loadOntologyFromAsset(assetFileName: String = "document_ontology.json"): DocumentOntologyTaxonomy = withContext(Dispatchers.IO) {
        cachedTaxonomy?.let { return@withContext it }

        val jsonString = context.assets.open(assetFileName).bufferedReader().use { it.readText() }
        val rootJson = JSONObject(jsonString)

        val ontologyId = rootJson.getString("ontologyId")
        val domainName = rootJson.getString("domainName")
        val version = rootJson.getString("version")

        val conceptsJsonArray = rootJson.getJSONArray("concepts")
        val conceptList = mutableListOf<DocumentConcept>()

        for (i in 0 until conceptsJsonArray.length()) {
            val cObj = conceptsJsonArray.getJSONObject(i)
            val classId = cObj.getString("classId")
            val className = cObj.getString("className")
            val parentClass = if (cObj.has("parentClass")) cObj.getString("parentClass") else null
            val description = cObj.getString("description")

            val kwArray = cObj.getJSONArray("lexicalKeywords")
            val keywords = mutableListOf<String>()
            for (k in 0 until kwArray.length()) {
                keywords.add(kwArray.getString(k))
            }

            val propArray = cObj.getJSONArray("properties")
            val properties = mutableListOf<OntologyProperty>()
            for (p in 0 until propArray.length()) {
                val pObj = propArray.getJSONObject(p)
                properties.add(
                    OntologyProperty(
                        name = pObj.getString("name"),
                        dataType = pObj.getString("dataType"),
                        domainClass = pObj.getString("domainClass"),
                        rangeClass = pObj.getString("rangeClass"),
                        isRequired = pObj.optBoolean("isRequired", true)
                    )
                )
            }

            conceptList.add(
                DocumentConcept(
                    classId = classId,
                    className = className,
                    parentClass = parentClass,
                    description = description,
                    lexicalKeywords = keywords,
                    properties = properties
                )
            )
        }

        val relationsJsonArray = rootJson.getJSONArray("relations")
        val relationList = mutableListOf<String>()
        for (r in 0 until relationsJsonArray.length()) {
            relationList.add(relationsJsonArray.getString(r))
        }

        val taxonomy = DocumentOntologyTaxonomy(
            ontologyId = ontologyId,
            domainName = domainName,
            version = version,
            concepts = conceptList,
            relations = relationList
        )
        cachedTaxonomy = taxonomy
        taxonomy
    }

    /**
     * Mengekstrak kalimat-kalimat dokumen hasil prapemrosesan NLP menjadi himpunan Fakta
     * Semantik Subjek-Predikat-Objek (SPO) berdasarkan pencocokan konsep taksonomi ontologi.
     */
    fun extractSemanticFacts(
        documentId: String,
        processedDoc: ProcessedDocumentText,
        taxonomy: DocumentOntologyTaxonomy
    ): List<ExtractedFact> {
        val facts = mutableListOf<ExtractedFact>()
        var factCounter = 1

        // Pola Regex Pengenal Entitas Dokumen Umum (Bahasa Indonesia)
        val docNumberRegex = Regex("""(?:Nomor|No\.|No)\s*[:=]?\s*([A-Za-z0-9/\-–.]+)""", RegexOption.IGNORE_CASE)
        val dateRegex = Regex("""(?:tanggal|ditetapkan\s*di\s*[A-Za-z]+,?\s*pada\s*tanggal|pada\s*tanggal)\s*[:=]?\s*(\d{1,2}\s+[A-Za-z]+\s+\d{4})""", RegexOption.IGNORE_CASE)
        val subjectTitleRegex = Regex("""(?:tentang|perihal|hal)\s*[:=]?\s*(.+)""", RegexOption.IGNORE_CASE)
        val issuerRegex = Regex("""(?i)(rektor|dekan|kepala|direktur|kementerian|yayasan|ketua\s*program\s*studi|badan\s*pengawas)\s*([A-Za-z\s]+)?""")
        val obligationRegex = Regex("""(?i)(wajib|diharuskan|wajib\s*mengikuti|harus|dimohon\s*untuk)\s+([^.,;]+)""")
        val deadlineRegex = Regex("""(?i)(paling\s*lambat|sampai\s*dengan|batas\s*akhir|tenggat\s*waktu|terakhir\s*pada)\s+([^.,;]+)""")
        val prohibitionRegex = Regex("""(?i)(dilarang|tidak\s*diperkenankan|tidak\s*boleh)\s+([^.,;]+)""")

        for (sentence in processedDoc.sentences) {
            val text = sentence.rawSentence

            // 1. Ekstraksi Nomor Dokumen
            docNumberRegex.find(text)?.let { match ->
                val numStr = match.groupValues[1].trim()
                if (numStr.length >= 3 && numStr.any { it.isDigit() }) {
                    facts.add(
                        ExtractedFact(
                            factId = "F_${factCounter++}",
                            documentId = documentId,
                            subject = "Dokumen",
                            predicate = "memilikiNomor",
                            objectValue = numStr,
                            category = FactCategory.METADATA_DOKUMEN,
                            sourceSentence = text,
                            pageNumber = sentence.pageNumber
                        )
                    )
                }
            }

            // 2. Ekstraksi Tanggal Dokumen
            dateRegex.find(text)?.let { match ->
                facts.add(
                    ExtractedFact(
                        factId = "F_${factCounter++}",
                        documentId = documentId,
                        subject = "Dokumen",
                        predicate = "diterbitkanPadaTanggal",
                        objectValue = match.groupValues[1].trim(),
                        category = FactCategory.METADATA_DOKUMEN,
                        sourceSentence = text,
                        pageNumber = sentence.pageNumber
                    )
                )
            }

            // 3. Ekstraksi Perihal / Judul Pokok
            subjectTitleRegex.find(text)?.let { match ->
                val titleStr = match.groupValues[1].trim()
                if (titleStr.length > 5) {
                    facts.add(
                        ExtractedFact(
                            factId = "F_${factCounter++}",
                            documentId = documentId,
                            subject = "Dokumen",
                            predicate = "memuatPerihal",
                            objectValue = titleStr,
                            category = FactCategory.METADATA_DOKUMEN,
                            sourceSentence = text,
                            pageNumber = sentence.pageNumber
                        )
                    )
                }
            }

            // 4. Ekstraksi Pihak Penerbit / Pejabat Penandatangan
            issuerRegex.find(text)?.let { match ->
                val issuerRole = match.groupValues[0].trim()
                facts.add(
                    ExtractedFact(
                        factId = "F_${factCounter++}",
                        documentId = documentId,
                        subject = "PihakPenerbit",
                        predicate = "memilikiKewenangan",
                        objectValue = issuerRole,
                        category = FactCategory.METADATA_DOKUMEN,
                        sourceSentence = text,
                        pageNumber = sentence.pageNumber
                    )
                )
            }

            // 5. Ekstraksi Klausul Kewajiban
            obligationRegex.find(text)?.let { match ->
                val obligationText = match.groupValues[2].trim()
                facts.add(
                    ExtractedFact(
                        factId = "F_${factCounter++}",
                        documentId = documentId,
                        subject = "PihakTerkait",
                        predicate = "memilikiKewajiban",
                        objectValue = obligationText,
                        category = FactCategory.KLAUSUL_KEWAJIBAN,
                        sourceSentence = text,
                        pageNumber = sentence.pageNumber
                    )
                )
            }

            // 6. Ekstraksi Batas Waktu / Tenggat Waktu (Deadline)
            deadlineRegex.find(text)?.let { match ->
                val deadlineText = match.groupValues[2].trim()
                facts.add(
                    ExtractedFact(
                        factId = "F_${factCounter++}",
                        documentId = documentId,
                        subject = "BatasWaktu",
                        predicate = "berakhirPada",
                        objectValue = deadlineText,
                        category = FactCategory.BATAS_WAKTU_DEADLINE,
                        sourceSentence = text,
                        pageNumber = sentence.pageNumber
                    )
                )
            }

            // 7. Ekstraksi Klausul Larangan
            prohibitionRegex.find(text)?.let { match ->
                val prohibitionText = match.groupValues[2].trim()
                facts.add(
                    ExtractedFact(
                        factId = "F_${factCounter++}",
                        documentId = documentId,
                        subject = "PihakTerkait",
                        predicate = "memilikiLarangan",
                        objectValue = prohibitionText,
                        category = FactCategory.KLAUSUL_LARANGAN,
                        sourceSentence = text,
                        pageNumber = sentence.pageNumber
                    )
                )
            }

            // 8. Pencocokan Konsep Taksonomi Ontologi Dokumen
            for (concept in taxonomy.concepts) {
                for (kw in concept.lexicalKeywords) {
                    if (text.contains(kw, ignoreCase = true)) {
                        facts.add(
                            ExtractedFact(
                                factId = "F_${factCounter++}",
                                documentId = documentId,
                                subject = "Dokumen",
                                predicate = "mengindikasikanKonsep",
                                objectValue = concept.className,
                                category = FactCategory.KLASIFIKASI_DOKUMEN,
                                sourceSentence = text,
                                pageNumber = sentence.pageNumber
                            )
                        )
                        break // Satu konsep per kalimat cukup
                    }
                }
            }
        }

        return facts
    }
}
