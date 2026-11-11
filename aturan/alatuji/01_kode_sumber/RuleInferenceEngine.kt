package com.njagakneai.velapdf.metopen.domain.inference

import android.content.Context
import com.njagakneai.velapdf.metopen.domain.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

/**
 * METOPEN TA 2026/2027 - PROYEK VELAPDF
 * Mahasiswa: Yogi Ario Pratama (NPM: 2313020004)
 * Dosen Pembimbing Proposal: Rony Heri Irawan, M.Kom.
 * 
 * Mesin Inferensi Berbasis Aturan (Rule-Based Inference Engine)
 * Menggunakan Algoritma Forward Chaining (Data-Driven Reasoning)
 * Bertugas mengevaluasi himpunan fakta semantik SPO terhadap basis aturan produksi IF-THEN
 * untuk menyimpulkan klasifikasi dokumen, kelengkapan aspek legal, dan klausul aksi.
 * 100% lokal di perangkat Android (Client-Side Runtime).
 */
class RuleInferenceEngine(
    private val context: Context
) {

    private val rulesDatabase = mutableListOf<ProductionRule>()

    /**
     * Memuat daftar aturan produksi IF-THEN dari berkas JSON di folder assets lokal.
     */
    suspend fun loadRulesFromAsset(assetFileName: String = "production_rules.json"): List<ProductionRule> = withContext(Dispatchers.IO) {
        if (rulesDatabase.isNotEmpty()) return@withContext rulesDatabase

        val jsonString = context.assets.open(assetFileName).bufferedReader().use { it.readText() }
        val rootJson = JSONObject(jsonString)
        val rulesArray = rootJson.getJSONArray("rules")

        val loadedRules = mutableListOf<ProductionRule>()

        for (i in 0 until rulesArray.length()) {
            val rObj = rulesArray.getJSONObject(i)
            val ruleId = rObj.getString("ruleId")
            val ruleName = rObj.getString("ruleName")
            val description = rObj.getString("description")
            val priority = rObj.optInt("priority", 1)

            val condArray = rObj.getJSONArray("conditions")
            val conditions = mutableListOf<RuleCondition>()
            for (c in 0 until condArray.length()) {
                val cObj = condArray.getJSONObject(c)
                val opStr = cObj.getString("operator").uppercase()
                val operator = when (opStr) {
                    "EQUALS", "==" -> OperatorType.EQUALS
                    "NOT_EQUALS", "!=" -> OperatorType.NOT_EQUALS
                    "CONTAINS" -> OperatorType.CONTAINS
                    "MATCHES_REGEX" -> OperatorType.MATCHES_REGEX
                    "EXISTS" -> OperatorType.EXISTS
                    else -> OperatorType.EQUALS
                }

                conditions.add(
                    RuleCondition(
                        factSubject = cObj.getString("subject"),
                        factPredicate = cObj.getString("predicate"),
                        operator = operator,
                        targetValue = cObj.optString("targetValue", "")
                    )
                )
            }

            val actObj = rObj.getJSONObject("action")
            val catStr = actObj.optString("category", "POIN_INTISARI")
            val category = when (catStr) {
                "KLASIFIKASI_DOKUMEN" -> FactCategory.KLASIFIKASI_DOKUMEN
                "KLAUSUL_KEWAJIBAN" -> FactCategory.KLAUSUL_KEWAJIBAN
                "KLAUSUL_LARANGAN" -> FactCategory.KLAUSUL_LARANGAN
                "BATAS_WAKTU_DEADLINE" -> FactCategory.BATAS_WAKTU_DEADLINE
                else -> FactCategory.POIN_INTISARI
            }

            val action = RuleAction(
                inferredSubject = actObj.getString("inferredSubject"),
                inferredPredicate = actObj.getString("inferredPredicate"),
                inferredObject = actObj.getString("inferredObject"),
                category = category,
                summaryBulletPoint = actObj.getString("summaryBulletPoint"),
                actionRequired = actObj.optString("actionRequired").takeIf { it.isNotBlank() }
            )

            loadedRules.add(
                ProductionRule(
                    ruleId = ruleId,
                    ruleName = ruleName,
                    description = description,
                    priority = priority,
                    conditions = conditions,
                    action = action
                )
            )
        }

        // Resolusi Konflik: Urutkan aturan berdasarkan tingkat prioritas menurun
        loadedRules.sortByDescending { it.priority }
        rulesDatabase.clear()
        rulesDatabase.addAll(loadedRules)
        loadedRules
    }

    /**
     * Mengeksekusi penalaran maju (Forward Chaining) terhadap fakta-fakta dalam Working Memory.
     */
    fun executeForwardChaining(
        documentId: String,
        initialFacts: List<ExtractedFact>
    ): InferenceResult {
        val startTime = System.currentTimeMillis()
        val workingMemory = initialFacts.toMutableList()
        val firedRules = mutableSetOf<String>()
        val traces = mutableListOf<InferenceTrace>()
        val inferredFacts = mutableListOf<ExtractedFact>()
        val bulletPoints = mutableListOf<String>()
        val actionItems = mutableListOf<String>()

        var newFactDeduced: Boolean
        var factCounter = 500

        // Siklus Iterasi Forward Chaining (Recognize-Act Cycle)
        do {
            newFactDeduced = false

            for (rule in rulesDatabase) {
                // Hindari penembakan ulang aturan yang sama
                if (firedRules.contains(rule.ruleId)) continue

                // Evaluasi apakah seluruh kondisi premis aturan terpenuhi oleh fakta dalam Working Memory
                val matchedFactsForRule = mutableListOf<ExtractedFact>()
                var allConditionsSatisfied = true

                for (condition in rule.conditions) {
                    val matchingFact = workingMemory.find { fact ->
                        isConditionSatisfied(condition, fact)
                    }

                    if (matchingFact != null) {
                        matchedFactsForRule.add(matchingFact)
                    } else {
                        allConditionsSatisfied = false
                        break
                    }
                }

                // Tembakkan aturan jika seluruh premis terpenuhi (Fire Rule)
                if (allConditionsSatisfied && matchedFactsForRule.isNotEmpty()) {
                    firedRules.add(rule.ruleId)

                    val newFact = ExtractedFact(
                        factId = "DED_${factCounter++}",
                        documentId = documentId,
                        subject = rule.action.inferredSubject,
                        predicate = rule.action.inferredPredicate,
                        objectValue = rule.action.inferredObject,
                        category = rule.action.category,
                        sourceSentence = "Deduksi Aturan: ${rule.ruleName}",
                        pageNumber = matchedFactsForRule.firstOrNull()?.pageNumber ?: 1
                    )

                    workingMemory.add(newFact)
                    inferredFacts.add(newFact)
                    bulletPoints.add(rule.action.summaryBulletPoint)
                    rule.action.actionRequired?.let { actionItems.add(it) }

                    traces.add(
                        InferenceTrace(
                            firedRuleId = rule.ruleId,
                            firedRuleName = rule.ruleName,
                            matchedFacts = matchedFactsForRule,
                            deductionResult = "${newFact.subject} -> ${newFact.predicate} : ${newFact.objectValue}",
                            executionTimestampMs = System.currentTimeMillis()
                        )
                    )

                    newFactDeduced = true
                    // Ulangi pencocokan dari aturan prioritas tertinggi dengan Working Memory terbaru
                    break
                }
            }
        } while (newFactDeduced)

        val duration = System.currentTimeMillis() - startTime

        return InferenceResult(
            documentId = documentId,
            executedRulesCount = rulesDatabase.size,
            firedRulesCount = firedRules.size,
            inferredFacts = inferredFacts,
            summaryBulletPoints = bulletPoints,
            actionItems = actionItems,
            inferenceTraces = traces,
            executionDurationMs = duration
        )
    }

    /**
     * Memeriksa pemenuhan operator logika kondisi terhadap fakta.
     */
    private fun isConditionSatisfied(condition: RuleCondition, fact: ExtractedFact): Boolean {
        // Cek kecocokan subjek (* berarti wildcard untuk sembarang subjek)
        if (condition.factSubject != "*" && !fact.subject.equals(condition.factSubject, ignoreCase = true)) {
            return false
        }
        // Cek kecocokan predikat (* berarti wildcard untuk sembarang predikat)
        if (condition.factPredicate != "*" && !fact.predicate.equals(condition.factPredicate, ignoreCase = true)) {
            return false
        }

        return when (condition.operator) {
            OperatorType.EQUALS -> fact.objectValue.equals(condition.targetValue, ignoreCase = true)
            OperatorType.NOT_EQUALS -> !fact.objectValue.equals(condition.targetValue, ignoreCase = true)
            OperatorType.CONTAINS -> fact.objectValue.contains(condition.targetValue, ignoreCase = true)
            OperatorType.MATCHES_REGEX -> Regex(condition.targetValue, RegexOption.IGNORE_CASE).containsMatchIn(fact.objectValue)
            OperatorType.EXISTS -> fact.objectValue.isNotBlank()
        }
    }
}
