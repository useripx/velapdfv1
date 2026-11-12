package com.njagakneai.velapdf.domain.ontology

import com.njagakneai.velapdf.domain.model.AssetTextProvider
import com.njagakneai.velapdf.domain.model.DocumentConcept
import com.njagakneai.velapdf.domain.model.DocumentOntologyTaxonomy
import com.njagakneai.velapdf.domain.model.ExtractedFact
import com.njagakneai.velapdf.domain.model.FactCategory
import com.njagakneai.velapdf.domain.model.LexicalClassificationConfig
import com.njagakneai.velapdf.domain.model.OntologyProperty
import com.njagakneai.velapdf.domain.model.OntologyPropertyBinding
import com.njagakneai.velapdf.domain.model.ProcessedDocumentText
import com.njagakneai.velapdf.domain.model.SpoPatternDictionary
import com.njagakneai.velapdf.domain.model.SpoPatternEntry
import com.njagakneai.velapdf.domain.util.SimpleJsonParser
import com.njagakneai.velapdf.domain.util.obj
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
 * (`spo_pattern_dictionary.json`) dengan pemetaan eksplisit ke kelas & properti ontologi.
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

            val classConfigObj = root.obj("lexicalClassificationConfig")
            val classificationConfig = if (classConfigObj.isNotEmpty()) {
                LexicalClassificationConfig(
                    emittedSubject = classConfigObj.str("emittedSubject", "Dokumen"),
                    emittedPredicate = classConfigObj.str("emittedPredicate", "mengindikasikanKonsep"),
                    formalClassNames = classConfigObj.strList("formalClassNames").toSet()
                        .ifEmpty { LexicalClassificationConfig().formalClassNames },
                    publicationClassNames = classConfigObj.strList("publicationClassNames").toSet()
                        .ifEmpty { LexicalClassificationConfig().publicationClassNames },
                    formalMinMatches = classConfigObj.optInt("formalMinMatches", 2),
                    nonFormalDominanceMargin = classConfigObj.optInt("nonFormalDominanceMargin", 1)
                )
            } else {
                LexicalClassificationConfig()
            }

            val taxonomy = DocumentOntologyTaxonomy(
                ontologyId = root.str("ontologyId", "ONT-VELAPDF"),
                domainName = root.str("domainName", "Dokumen VelaPDF"),
                version = root.str("version", "2.2.0"),
                concepts = concepts,
                relations = root.strList("relations"),
                classificationConfig = classificationConfig
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
                ontologyClass = pObj.str("ontologyClass").takeIf { it.isNotBlank() },
                ontologyProperty = pObj.str("ontologyProperty").takeIf { it.isNotBlank() },
                ontologyRelation = pObj.str("ontologyRelation").takeIf { it.isNotBlank() },
                category = parseFactCategory(pObj.str("category", "POIN_INTISARI")),
                regexPattern = pObj.str("regexPattern"),
                objectGroup = pObj.optInt("objectGroup", 1),
                minObjectLength = pObj.optInt("minObjectLength", 3),
                maxObjectLength = pObj.optInt("maxObjectLength", 200),
                requireDigitInObject = pObj.optBool("requireDigitInObject", false),
                negationGuards = pObj.strList("negationGuards").map { it.lowercase() }
            )
        }

        val dict = SpoPatternDictionary(
            dictionaryId = root.str("dictionaryId", "SPO-VELAPDF"),
            version = root.str("version", "1.1.0"),
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
        val activeSpoDict = spoDictionary ?: SpoPatternDictionary("EMPTY", "1.1.0", emptyList())
        val compiledPatterns = activeSpoDict.patterns.map { entry ->
            entry to Regex(entry.regexPattern)
        }
        val classConfig = taxonomy.classificationConfig

        val facts = mutableListOf<ExtractedFact>()
        val seenTriples = mutableSetOf<Triple<String, String, String>>()
        var factCounter = 1

        fun addUniqueFact(
            subject: String,
            predicate: String,
            objectValue: String,
            category: FactCategory,
            sourceSentence: String,
            pageNumber: Int,
            sourceMethod: String,
            ontologyClass: String? = null,
            ontologyProperty: String? = null,
            ontologyRelation: String? = null
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
                        pageNumber = pageNumber,
                        sourceMethod = sourceMethod,
                        ontologyClass = ontologyClass,
                        ontologyProperty = ontologyProperty,
                        ontologyRelation = ontologyRelation
                    )
                )
            }
        }

        // Tahap 3a: Klasifikasi Leksikal Ontologi (memproduksi fakta `mengindikasikanKonsep` sesuai config JSON)
        val fullTextLower = processedDoc.sentences.joinToString("\n") { it.rawSentence.lowercase() }
        val conceptScores = mutableMapOf<String, Int>()
        val conceptFirstPage = mutableMapOf<String, Int>()
        val conceptFirstSentence = mutableMapOf<String, String>()

        for (concept in taxonomy.concepts) {
            if (concept.parentClass == null && concept.className in classConfig.formalClassNames) continue
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

        val formalBestEntry = conceptScores.entries
            .filter { it.key in classConfig.formalClassNames }
            .maxByOrNull { it.value }
        val pubBestEntry = conceptScores.entries
            .filter { it.key in classConfig.publicationClassNames }
            .maxByOrNull { it.value }

        val formalScore = formalBestEntry?.value ?: 0
        val pubScore = pubBestEntry?.value ?: 0

        val isFormalDocument = when {
            formalScore == 0 -> false
            pubScore == 0 -> formalScore >= 1
            pubBestEntry?.key == "ArtikelJurnalIlmiah" && pubScore >= 3 && formalScore < 4 -> false
            pubScore >= formalScore + classConfig.nonFormalDominanceMargin -> false
            formalScore >= classConfig.formalMinMatches && formalScore >= pubScore -> true
            else -> false
        }

        if (isFormalDocument && formalBestEntry != null) {
            addUniqueFact(
                subject = classConfig.emittedSubject,
                predicate = classConfig.emittedPredicate,
                objectValue = formalBestEntry.key,
                category = FactCategory.KLASIFIKASI_DOKUMEN,
                sourceSentence = conceptFirstSentence[formalBestEntry.key].orEmpty(),
                pageNumber = conceptFirstPage[formalBestEntry.key] ?: 1,
                sourceMethod = "ONTOLOGY_LEXICAL_CLASSIFICATION",
                ontologyClass = formalBestEntry.key,
                ontologyProperty = "subClassOf:DokumenResmi",
                ontologyRelation = classConfig.emittedPredicate
            )
        } else if (pubBestEntry != null) {
            addUniqueFact(
                subject = classConfig.emittedSubject,
                predicate = classConfig.emittedPredicate,
                objectValue = pubBestEntry.key,
                category = FactCategory.KLASIFIKASI_DOKUMEN,
                sourceSentence = conceptFirstSentence[pubBestEntry.key].orEmpty(),
                pageNumber = conceptFirstPage[pubBestEntry.key] ?: 1,
                sourceMethod = "ONTOLOGY_LEXICAL_CLASSIFICATION",
                ontologyClass = pubBestEntry.key,
                ontologyProperty = null,
                ontologyRelation = classConfig.emittedPredicate
            )
        }

        // Tahap 3b: Ekstraksi Fakta SPO dari Kamus Pola JSON dengan anti-negasi guard & pemetaan properti
        for (sentence in processedDoc.sentences) {
            val text = sentence.rawSentence
            for ((entry, regex) in compiledPatterns) {
                if (isFormalDocument && entry.domainScope == "BOOK") continue

                for (match in regex.findAll(text)) {
                    if (isPrecededByNegation(text, match.range.first, entry.negationGuards)) {
                        continue
                    }

                    val rawObj = match.groupValues.getOrNull(entry.objectGroup)?.trim().orEmpty()
                    if (rawObj.length !in entry.minObjectLength..entry.maxObjectLength) continue
                    if (entry.requireDigitInObject && !rawObj.any { it.isDigit() }) continue

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
                        pageNumber = sentence.pageNumber,
                        sourceMethod = "SPO_PATTERN_REGEX",
                        ontologyClass = entry.ontologyClass,
                        ontologyProperty = entry.ontologyProperty,
                        ontologyRelation = entry.ontologyRelation
                    )
                }
            }
        }

        return facts
    }

    /**
     * Memetakan fakta SPO terekstrak ke definisi properti kelas Ontologi di `document_ontology.json`,
     * serta mengecek kelengkapan properti wajib (`isRequired = true`) untuk kelas terdeteksi.
     */
    fun mapFactsToOntologyProperties(
        taxonomy: DocumentOntologyTaxonomy,
        facts: List<ExtractedFact>
    ): Pair<List<OntologyPropertyBinding>, List<String>> {
        val conceptByName = taxonomy.concepts.associateBy { it.className }
        val detectedClassName = facts.firstOrNull { it.predicate == "mengindikasikanKonsep" }?.objectValue
        val detectedConcept = detectedClassName?.let { conceptByName[it] }
        val parentConcept = detectedConcept?.parentClass?.let { conceptByName[it] }

        val relevantProperties = buildList {
            parentConcept?.properties?.let { addAll(it) }
            detectedConcept?.properties?.let { addAll(it) }
        }
        val propertyDefMap = relevantProperties.associateBy { it.name }

        val bindings = mutableListOf<OntologyPropertyBinding>()
        val seenPairs = mutableSetOf<Pair<String, String>>()

        for (fact in facts) {
            val propName = fact.ontologyProperty ?: continue
            val className = fact.ontologyClass ?: detectedClassName ?: continue
            val def = propertyDefMap[propName]
                ?: taxonomy.concepts.flatMap { it.properties }.firstOrNull { it.name == propName }

            val key = className to propName
            if (seenPairs.add(key)) {
                bindings.add(
                    OntologyPropertyBinding(
                        className = def?.domainClass ?: className,
                        propertyName = propName,
                        spoPredicate = fact.predicate,
                        extractedValue = fact.objectValue,
                        pageNumber = fact.pageNumber,
                        isRequired = def?.isRequired ?: false
                    )
                )
            }
        }

        val filledPropertyNames = bindings.map { it.propertyName }.toSet()
        val missingRequired = relevantProperties
            .filter { it.isRequired && it.name !in filledPropertyNames }
            .map { "${it.domainClass}.${it.name}" }
            .distinct()

        return bindings to missingRequired
    }

    private fun isPrecededByNegation(text: String, matchStart: Int, guards: List<String>): Boolean {
        if (guards.isEmpty() || matchStart <= 0) return false
        val windowStart = (matchStart - 28).coerceAtLeast(0)
        val prefix = text.substring(windowStart, matchStart).lowercase().trimEnd()
        return guards.any { guard ->
            prefix.endsWith(guard) &&
                (prefix.length == guard.length || !prefix[prefix.length - guard.length - 1].isLetterOrDigit())
        }
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
