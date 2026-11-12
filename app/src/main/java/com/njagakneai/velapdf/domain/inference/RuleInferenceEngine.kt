package com.njagakneai.velapdf.domain.inference

import com.njagakneai.velapdf.domain.model.AssetTextProvider
import com.njagakneai.velapdf.domain.model.ExtractedFact
import com.njagakneai.velapdf.domain.model.FactCategory
import com.njagakneai.velapdf.domain.model.InferenceResult
import com.njagakneai.velapdf.domain.model.InferenceTrace
import com.njagakneai.velapdf.domain.model.OperatorType
import com.njagakneai.velapdf.domain.model.ProductionRule
import com.njagakneai.velapdf.domain.model.RuleAction
import com.njagakneai.velapdf.domain.model.RuleCondition
import com.njagakneai.velapdf.domain.util.SimpleJsonParser
import com.njagakneai.velapdf.domain.util.obj
import com.njagakneai.velapdf.domain.util.objList
import com.njagakneai.velapdf.domain.util.optInt
import com.njagakneai.velapdf.domain.util.str
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mesin Inferensi Berbasis Aturan (Forward Chaining Engine) yang berjalan 100% luring
 * dengan memuat aturan produksi IF-THEN dari berkas JSON `production_rules.json`.
 */
@Singleton
class RuleInferenceEngine @Inject constructor(
    private val assetProvider: AssetTextProvider
) {

    private var cachedRules: List<ProductionRule> = emptyList()

    suspend fun loadRulesFromAsset(assetFileName: String = "production_rules.json"): List<ProductionRule> =
        withContext(Dispatchers.IO) {
            if (cachedRules.isNotEmpty()) return@withContext cachedRules

            val jsonString = assetProvider.readAsset(assetFileName)
            val root = SimpleJsonParser.parseObject(jsonString)

            val loaded = root.objList("rules").map { rObj ->
                val conditions = rObj.objList("conditions").map { cObj ->
                    val opStr = cObj.str("operator", "EQUALS").uppercase()
                    val operator = when (opStr) {
                        "EQUALS", "==" -> OperatorType.EQUALS
                        "NOT_EQUALS", "!=" -> OperatorType.NOT_EQUALS
                        "CONTAINS" -> OperatorType.CONTAINS
                        "MATCHES_REGEX" -> OperatorType.MATCHES_REGEX
                        "EXISTS" -> OperatorType.EXISTS
                        else -> OperatorType.EQUALS
                    }
                    RuleCondition(
                        factSubject = cObj.str("subject"),
                        factPredicate = cObj.str("predicate"),
                        operator = operator,
                        targetValue = cObj.str("targetValue")
                    )
                }

                val actObj = rObj.obj("action")
                val category = when (actObj.str("category", "POIN_INTISARI").uppercase()) {
                    "KLASIFIKASI_DOKUMEN" -> FactCategory.KLASIFIKASI_DOKUMEN
                    "KLAUSUL_KEWAJIBAN" -> FactCategory.KLAUSUL_KEWAJIBAN
                    "KLAUSUL_LARANGAN" -> FactCategory.KLAUSUL_LARANGAN
                    "KLAUSUL_HAK" -> FactCategory.KLAUSUL_HAK
                    "BATAS_WAKTU_DEADLINE" -> FactCategory.BATAS_WAKTU_DEADLINE
                    else -> FactCategory.POIN_INTISARI
                }

                ProductionRule(
                    ruleId = rObj.str("ruleId"),
                    ruleName = rObj.str("ruleName"),
                    description = rObj.str("description"),
                    priority = rObj.optInt("priority", 1),
                    conditions = conditions,
                    action = RuleAction(
                        inferredSubject = actObj.str("inferredSubject"),
                        inferredPredicate = actObj.str("inferredPredicate"),
                        inferredObject = actObj.str("inferredObject"),
                        category = category,
                        summaryBulletPoint = actObj.str("summaryBulletPoint"),
                        actionRequired = actObj.str("actionRequired").takeIf { it.isNotBlank() }
                    )
                )
            }.sortedByDescending { it.priority }

            cachedRules = loaded
            loaded
        }

    fun executeForwardChaining(
        documentId: String,
        initialFacts: List<ExtractedFact>,
        rules: List<ProductionRule> = cachedRules
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

        do {
            newFactDeduced = false
            for (rule in rules) {
                if (firedRules.contains(rule.ruleId)) continue

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
                        pageNumber = matchedFactsForRule.firstOrNull()?.pageNumber ?: 1,
                        sourceMethod = "FORWARD_CHAINING_INFERENCE",
                        ontologyRelation = rule.action.inferredPredicate
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
                    break
                }
            }
        } while (newFactDeduced)

        return InferenceResult(
            documentId = documentId,
            executedRulesCount = rules.size,
            firedRulesCount = firedRules.size,
            inferredFacts = inferredFacts,
            summaryBulletPoints = bulletPoints,
            actionItems = actionItems,
            inferenceTraces = traces,
            executionDurationMs = System.currentTimeMillis() - startTime
        )
    }

    private fun isConditionSatisfied(condition: RuleCondition, fact: ExtractedFact): Boolean {
        if (condition.factSubject != "*" &&
            !fact.subject.equals(condition.factSubject, ignoreCase = true)
        ) {
            return false
        }
        if (condition.factPredicate != "*" &&
            !fact.predicate.equals(condition.factPredicate, ignoreCase = true)
        ) {
            return false
        }

        return when (condition.operator) {
            OperatorType.EQUALS -> fact.objectValue.equals(condition.targetValue, ignoreCase = true)
            OperatorType.NOT_EQUALS -> !fact.objectValue.equals(condition.targetValue, ignoreCase = true)
            OperatorType.CONTAINS -> fact.objectValue.contains(condition.targetValue, ignoreCase = true)
            OperatorType.MATCHES_REGEX ->
                Regex(condition.targetValue, RegexOption.IGNORE_CASE).containsMatchIn(fact.objectValue)
            OperatorType.EXISTS -> fact.objectValue.isNotBlank()
        }
    }
}
