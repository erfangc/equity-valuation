package com.erfangc.equity.valuation.yahoo.financials

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate

data class Financial(
    @JsonFormat(pattern = "yyyy-MM-dd")
    val date: LocalDate,
    val incomeStatement: Map<String, Double>,
    val balanceSheet: Map<String, Double>,
    val cashflowStatement: Map<String, Double>
) {
    companion object {

        object CashflowStatement {
            const val NetIncome = "NetIncome"
            const val Depreciation = "Depreciation"
            const val AdjustmentsToNetIncome = "AdjustmentsToNetIncome"
            const val ChangesInAccountsReceivables = "ChangesInAccountsReceivables"
            const val ChangesInLiabilities = "ChangesInLiabilities"
            const val ChangesInInventories = "ChangesInInventories"
            const val ChangesInOtherOperatingActivities = "ChangesInOtherOperatingActivities"
            const val TotalCashFlowFromOperatingActivities = "TotalCashFlowFromOperatingActivities"
            const val CapitalExpenditures = "CapitalExpenditures"
            const val Investments = "Investments"
            const val OtherCashFlowsFromInvestingActivities = "OtherCashFlowsFromInvestingActivities"
            const val TotalCashFlowsFromInvestingActivities = "TotalCashFlowsFromInvestingActivities"
            const val DividendsPaid = "DividendsPaid"
            const val NetBorrowings = "NetBorrowings"
            const val OtherCashFlowsFromFinancingActivities = "OtherCashFlowsFromFinancingActivities"
            const val TotalCashFlowsFromFinancingActivities = "TotalCashFlowsFromFinancingActivities"
            const val ChangeInCashAndCashEquivalents = "ChangeInCashAndCashEquivalents"
        }

        object BalanceSheet {
            const val CashAndCashEquivalents = "CashAndCashEquivalents"
            const val NetReceivables = "NetReceivables"
            const val Inventory = "Inventory"
            const val TotalCurrentAssets = "TotalCurrentAssets"
            const val PropertyPlantAndEquipment = "PropertyPlantAndEquipment"
            const val Goodwill = "Goodwill"
            const val IntangibleAssets = "IntangibleAssets"
            const val OtherAssets = "OtherAssets"
            const val TotalAssets = "TotalAssets"
            const val AccountsPayable = "AccountsPayable"
            const val TotalCurrentLiabilities = "TotalCurrentLiabilities"
            const val LongTermDebt = "LongTermDebt"
            const val OtherLiabilities = "OtherLiabilities"
            const val TotalLiabilities = "TotalLiabilities"
            const val CommonStock = "CommonStock"
            const val TotalStockholderEquity = "TotalStockholderEquity"
            const val NetTangibleAssets = "NetTangibleAssets"
        }

        object IncomeStatement {
            const val TotalRevenue = "TotalRevenue"
            const val CostOfRevenue = "CostOfRevenue"
            const val GrossProfit = "GrossProfit"
            const val ResearchDevelopment = "ResearchDevelopment"
            const val SellingGeneralAndAdministrative = "SellingGeneralAndAdministrative"
            const val TotalOperatingExpenses = "TotalOperatingExpenses"
            const val OperatingIncomeOrLoss = "OperatingIncomeOrLoss"
            const val TotalOtherIncome = "TotalOtherIncome/expensesNet"
            const val EarningsBeforeInterestAndTaxes = "EarningsBeforeInterestAndTaxes"
            const val InterestExpense = "InterestExpense"
            const val IncomeBeforeTax = "IncomeBeforeTax"
            const val IncomeTaxExpense = "IncomeTaxExpense"
            const val MinorityInterest = "MinorityInterest"
            const val NetIncomeFromContinuingOps = "NetIncomeFromContinuingOps"
            const val NetIncome = "NetIncome"
            const val NetIncomeApplicableToCommonShares = "NetIncomeApplicableToCommonShares"
        }
    }
}
