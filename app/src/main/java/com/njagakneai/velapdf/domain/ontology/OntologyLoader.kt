package com.njagakneai.velapdf.domain.ontology

import com.njagakneai.velapdf.domain.model.AssetTextProvider
import com.njagakneai.velapdf.domain.model.DocumentConcept
import com.njagakneai.velapdf.domain.model.DocumentOntologyTaxonomy
import com.njagakneai.velapdf.domain.model.ExtractedFact
import com.njagakneai.velapdf.domain.model.FactCategory
import com.njagakneai.velapdf.domain.model.OntologyProperty
import com.njagakneai.velapdf.domain.model.ProcessedDocumentText
import com.njagakneai.velapdf.domain.model.SpoPatternDictionary
import com.njagakneai.velapdf.domain.model.SpoPatternEntry
import com.njagakneai.velapdf.domain.util.SimpleJsonParser
import com.njagakneai.velapdf.domain.util.objList
import com.njagakneai.velapdf.domain.util.optBool
import com.njagakneai.velapdf.domain.util.optInt
import com.njagakneai.velapdf.domain.util.str
import com.njagakneai.velapdf.domain.util.strList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Pemuat Ontologi Konsep Dokumen (`document_ontology.json`) dan Kamus Pola Ekstraksi SPO
 * (`spo_pattern_dictionary.json`) berbasis data JSON sepenuhnya.
 */
@Singleton
class OntologyLoader @Inject constructor(
    private val assetProvider: AssetTextProvider
) {

    private var cachedTaxonomy: DocumentOntologyTaxonomy? = null
    private var cachedSpoDictionary: SpoPatternDictionary? = null

    suspend fun loadOntologyFromAsset(assetFileName: String = "document_ontology.json"): DocumentOntologyTaxonomy =
        withContext(Dispatchers.IO) {
            cachedTaxonomy?.let { return@withContext it }

            val jsonString = assetProvider.readAsset(assetFileName)
            val root = SimpleJsonParser.parseObject(jsonString)

            val concepts = root.objList("concepts").map { cObj ->
                val properties = cObj.objList("properties").map { pObj ->
                    OntologyProperty(
                        name = pObj.str("name"),
                        dataType = pObj.str("dataType"),
                        domainClass = pObj.str("domainClass"),
                        rangeClass = pObj.str("rangeClass"),
                        isRequired = pObj.optBool("isRequired", true)
                    )
                }
                DocumentConcept(
                    classId = cObj.str("classId"),
                    className = cObj.str("className"),
                    parentClass = cObj["parentClass"] as? String,
                    description = cObj.str("description"),
                    lexicalKeywords = cObj.strList("lexicalKeywords"),
                    properties = properties
                )
            }

            val taxonomy = DocumentOntologyTaxonomy(
                ontologyId = root.str("ontologyId", "ONT-VELAPDF"),
                domainName = root.str("domainName", "Dokumen VelaPDF"),
                version = root.str("version", "2.1.0"),
                concepts = concepts,
                relations = root.strList("relations")
            )
            cachedTaxonomy = taxonomy
            taxonomy
        }

    suspend fun loadSpoDictionaryFromAsset(
        assetFileName: String = "spo_pattern_dictionary.json"
    ): SpoPatternDictionary = withContext(Dispatchers.IO) {
        cachedSpoDictionary?.let { return@withContext it }

        val jsonString = assetProvider.readAsset(assetFileName)
        val root = SimpleJsonParser.parseObject(jsonString)

        val patterns = root.objList("patterns").map { pObj ->
            SpoPatternEntry(
                patternId = pObj.str("patternId"),
                domainScope = pObj.str("domainScope", "ALL"),
                description = pObj.str("description"),
                subject = pObj.str("subject"),
                subjectGroup = pObj.optInt("subjectGroup", 0),
                predicate = pObj.str("predicate"),
                category = parseFactCategory(pObj.str("category", "POIN_INTISARI")),
                regexPattern = pObj.str("regexPattern"),
                objectGroup = pObj.optInt("objectGroup", 1),
                minObjectLength = pObj.optInt("minObjectLength", 3),
                maxObjectLength = pObj.optInt("maxObjectLength", 200),
                requireDigitInObject = pObj.optBool("requireDigitInObject", false)
            )
        }

        val dict = SpoPatternDictionary(
            dictionaryId = root.str("dictionaryId", "SPO-VELAPDF"),
            version = root.str("version", "1.0.0"),
            patterns = patterns
        )
        cachedSpoDictionary = dict
        dict
    }

    fun extractSemanticFacts(
        documentId: String,
        processedDoc: ProcessedDocumentText,
        taxonomy: DocumentOntologyTaxonomy,
        spoDictionary: SpoPatternDictionary? = cachedSpoDictionary
    ): List<ExtractedFact> {
        val activeSpoDict = spoDictionary ?: SpoPatternDictionary("EMPTY", "1.0", emptyList())
        val compiledPatterns = activeSpoDict.patterns.map { entry ->
            entry to Regex(entry.regexPattern)
        }

        val facts = mutableListOf<ExtractedFact>()
        val seenTriples = mutableSetOf<Triple<String, String, String>>()
        var factCounter = 1

        fun addUniqueFact(
            subject: String,
            predicate: String,
            objectValue: String,
            category: FactCategory,
            sourceSentence: String,
            pageNumber: Int
        ) {
            val normalizedObj = objectValue.trim().trimEnd('.', ',', ';', ':')
            if (normalizedObj.isBlank()) return
            val key = Triple(subject.lowercase(), predicate.lowercase(), normalizedObj.lowercase())
            if (seenTriples.add(key)) {
                facts.add(
                    ExtractedFact(
                        factId = "F_${factCounter++}",
                        documentId = documentId,
                        subject = subject,
                        predicate = predicate,
                        objectValue = normalizedObj,
                        category = category,
                        sourceSentence = sourceSentence,
                        pageNumber = pageNumber
                    )
                )
            }
        }

        // 1. Skor konsep ontologi di seluruh naskah agar klasifikasi formal vs buku tetap akurat
        val fullTextLower = processedDoc.sentences.joinToString("\n") { it.rawSentence.lowercase() }
        val conceptScores = mutableMapOf<String, Int>()
        val conceptFirstPage = mutableMapOf<String, Int>()
        val conceptFirstSentence = mutableMapOf<String, String>()

        for (concept in taxonomy.concepts) {
            if (concept.className == "DokumenResmi") continue
            val matchedKeywords = concept.lexicalKeywords.filter { kw ->
                containsKeyword(fullTextLower, kw.lowercase())
            }
            if (matchedKeywords.isNotEmpty()) {
                conceptScores[concept.className] = matchedKeywords.size
                val hitSentence = processedDoc.sentences.firstOrNull { s ->
                    matchedKeywords.any { kw -> containsKeyword(s.rawSentence.lowercase(), kw.lowercase()) }
                }
                conceptFirstPage[concept.className] = hitSentence?.pageNumber ?: 1
                conceptFirstSentence[concept.className] = hitSentence?.rawSentence ?: ""
            }
        }

        val formalClasses = setOf("SuratKeputusan", "SuratEdaran", "PedomanSOP", "PeraturanRegulasi", "LaporanResmi")
        val formalBestEntry = conceptScores.entries
            .filter { it.key in formalClasses }
            .maxByOrNull { it.value }
        val bookBestEntry = conceptScores.entries
            .filter { it.key in setOf("BukuAjarTeknis", "DokumenInformatifUmum") }
            .maxByOrNull { it.value }

        val isFormalDocument = (formalBestEntry?.value ?: 0) >= 2 ||
            ((formalBestEntry?.value ?: 0) >= 1 && (bookBestEntry?.value ?: 0) <= (formalBestEntry?.value ?: 0))

        // Emit konsep dominan sebagai fakta ontologi
        if (isFormalDocument && formalBestEntry != null) {
            addUniqueFact(
                subject = "Dokumen",
                predicate = "mengindikasikanKonsep",
                objectValue = formalBestEntry.key,
                category = FactCategory.KLASIFIKASI_DOKUMEN,
                sourceSentence = conceptFirstSentence[formalBestEntry.key].orEmpty(),
                pageNumber = conceptFirstPage[formalBestEntry.key] ?: 1
            )
        } else if (bookBestEntry != null) {
            addUniqueFact(
                subject = "Dokumen",
                predicate = "mengindikasikanKonsep",
                objectValue = bookBestEntry.key,
                category = FactCategory.KLASIFIKASI_DOKUMEN,
                sourceSentence = conceptFirstSentence[bookBestEntry.key].orEmpty(),
                pageNumber = conceptFirstPage[bookBestEntry.key] ?: 1
            )
        }

        // 2. Ekstraksi Fakta SPO dari Kamus Pola JSON
        for (sentence in processedDoc.sentences) {
            val text = sentence.rawSentence
            for ((entry, regex) in compiledPatterns) {
                if (isFormalDocument && entry.domainScope == "BOOK") continue

                regex.find(text)?.let { match ->
                    val rawObj = match.groupValues.getOrNull(entry.objectGroup)?.trim().orEmpty()
                    if (rawObj.length !in entry.minObjectLength..entry.maxObjectLength) return@let
                    if (entry.requireDigitInObject && !rawObj.any { it.isDigit() }) return@let

                    val resolvedSubject = if (entry.subjectGroup > 0) {
                        val capturedSubj = match.groupValues.getOrNull(entry.subjectGroup)?.trim().orEmpty()
                        if (capturedSubj.length in 2..45) cleanSubjectLabel(capturedSubj) else entry.subject
                    } else {
                        entry.subject
                    }

                    addUniqueFact(
                        subject = resolvedSubject,
                        predicate = entry.predicate,
                        objectValue = rawObj,
                        category = entry.category,
                        sourceSentence = text,
                        pageNumber = sentence.pageNumber
                    )
                }
            }
        }

        return facts
    }

    private fun containsKeyword(textLower: String, keywordLower: String): Boolean {
        val pattern = "\\b" + Regex.escape(keywordLower) + "\\b"
        return Regex(pattern).containsMatchIn(textLower)
    }

    private fun cleanSubjectLabel(raw: String): String {
        val trimmed = raw.trim().trim(':', '-', ',', '.')
        return if (trimmed.length > 38) trimmed.take(38).trim() else trimmed
    }

    private fun parseFactCategory(value: String): FactCategory {
        return when (value.uppercase()) {
            "METADATA_DOKUMEN" -> FactCategory.METADATA_DOKUMEN
            "KLASIFIKASI_DOKUMEN" -> FactCategory.KLASIFIKASI_DOKUMEN
            "KLAUSUL_KEWAJIBAN" -> FactCategory.KLAUSUL_KEWAJIBAN
            "KLAUSUL_LARANGAN" -> FactCategory.KLAUSUL_LARANGAN
            "KLAUSUL_HAK" -> FactCategory.KLAUSUL_HAK
            "BATAS_WAKTU_DEADLINE" -> FactCategory.BATAS_WAKTU_DEADLINE
            else -> FactCategory.POIN_INTISARI
        }
    }
}
