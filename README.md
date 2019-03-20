# Where does the stock market think society is headed? (Answered using Kotlin, Elasticsearch)

In this article I try to answer the question, as far as the investment community is concerned,
what is _going to be_ the fastest growing industries in the United States? I will then discuss some implications
of these findings

When investors suddenly believe a company's profit will grow substantially, the company's stock price would increase
regardless of the company's profitability today. This logic is simple enough. However, this intuitive relationship between growth and stock price is complicated by the fact that _unpredictability_ of profits also affect
stock prices _today_. In finance, we say this unpredictability give rise to a _risk premium_ or a required discount
to compensate for the aforementioned unpredictability. 
Thus, we must isolate and remove the effect of this risk premium when calculating expected growth. We are going to answer these question using `Kotlin` and scrapping some data from the web

We will introduce the topic and hands-on coding in the following order:

1. Walk through an example of how to compute the growth rate for a single company

2. Show you how to perform the analysis for every company in the United States using Kotlin, JSoup and Yahoo Finance

3. Summarize our data using Elasticsearch

The model we use here is quite boorish and grossly simplified, but there are still two major benefits to doing this exercise:
1. As part of this exercise we learn how to store company financial statements and metrics in a readily consumable format.
This should enable us to perform more thorough and nuanced financial analysis in code going forward
2. Simple models applied to large numbers of companies can still yield macroscopic insights. This is true if we assume variations across individual companies
not captured by the simple models cancel each other out.

## Disclaimer

First, a disclaimer:
    
    The content of this article does not constitute advice or recommendation to purchase any specific financial security. 
    By showing you simplified academic models for valuing financial securities I am encouraging you to perform your own research 
    and arrive at independent decisions. Even if you are inspired to invest, please consult a qualified financial professional and consider the risk 
    and reward appropriate for your personal situation before investing.
    
    All financial models, including (and especially) the ones described in this article, are valid only under 
    a variety of assumptions, when these assumptions do not hold real life results can departure significantly from
    theoretical predictions.
    
    In fact, a central assumption to the concepts described in this article is that markets are somewhat efficiently
    pricing these stocks therefore no abnormal profits can be gained from using the tools described here alone 

## A Crash Course on Stock Valuation

Apple Inc. (NASDAQ: AAPL) has a market capitalization of 879.54 billion dollars (186.53 per share X 4.72 billion shares outstanding)
at the time of this writing. This means you would need to pay this amount to full own the company.

    For $879.54 Billion Dollars, You can Buy Apple Inc. and be entitled to all of its current and feature wealth
    
So far:

|              |              |
|---           |---           |
| Cost         | $879 Billion |
| Benefit      | $0           |
| Unexplained  | $879 Billion |

Let's break this number down a bit:

The company currently have: ~$131 billion USD in cash, short-term investments and inventory. This means, if shareholders cleared Apple's bank account, sold all of the stocks & US government bonds Apple owns as well as all
of its excess inventory of iPhones, iPads & MacBooks we would end up with ~$131 billion dollars. These should be relatively
easy to sell and monetize

Further, if Apple divested all of its investments in other companies, patents & longer-term government bonds we would receive another $141.7 billion
in cash. So if we sold every piece of valuable at the company today we'd end up with about $365.7 billion in hard cold cash. Apple currently have debts ~$258 billion (~$116 billion due in the next year). 

Assuming we pay down all of Apple's debt with the $365.7 billion we received from liquidating the company, we will end up
with a net of ~$107 billion dollars on our hands. Therefore, Apple Inc., if it stopped operating _today_ and closed up shop, shareholders
would immediately receive ~107 billion dollars or about ~$22 per share. Recall that the total valuation of Apple is $879.54 billion, of which only $107 billion or roughly just 12% the net wealth
of the company come from the net cash the company have already pocketed.

With that, let's do a progress check:

|                   |             |
|---                | ---         |
| Cost              | $879 Billion|
| Benefits:         |             |
| Liquidation Value | 107 Billion |
| Unexplained       | 772 Billion |

Of course, investors are not stupid. No one would trade something that is worth 879.54 billion for something is only worth 107 billion.

This leaves $772 billion of Apple Inc. valuation unaccounted for. This $772 billion in valuation comes from the profits we expect Apple to earn
in the future. This is a critically important concept to understand: when you invest in a stock, you are investing in both what the 
company has already pocketed and what you believe the company will pocket in the future.

Next we will breakdown this $772 billion in expected future profit. 
For that we need a way to quantity the future profitability of Apple, thus we need to introduce a simple valuation model
known as the `Dividend Discount Model` and a close variation: the `Growing Perpetuity model`.

### Valuation Model

For a full analysis of Apple's future financial prospects, we would need to project Apple's 
financial statement going forward in near and long terms. For that exercise, we'd have to make projections on

 - Product sales based on historical trends as well as projected competition & consumer trends 
 - Cost of producing products
 - Research and Development
 - Misc costs like administration & taxes

That is a lot to go through for one article, so let's look at an alternative model that only consider the _net effect_ of
these various factors: _profit_ (i.e. earnings) 

In 2018, AAPL's profit (sales - all cost) is roughly $59 billion dollars.

According to the dividend discount model:
```
# E = Profit made today (or in the original model, the dividend paid, but we don't have to worry about that for now)
# r = Required rate of return
# P = Value Today
P = E / r # According to the (simplified) dividend discount model
``` 

Since Apple is a large and somewhat stable company that tends to perform inline with the broad economy, I would give AAPL
a required rate of return of roughly equal to that of the market or 8.2%

If you are unfamiliar with required rate of return and risk premiums think of it this way:
Even if we assume the $59 billion Apple Inc. is earning now can be repeated year after year. Our confidence in that projection decrease
over time. Plus money made 10 years from now is not worth waiting for as money we can obtain today. Therefore, to give up
 $879.2 billion dollars today in exchange for a stream of $59 billions, I must "discount" these projections by
demanding that I make at least 8.2% yearly on average assuming the base of $59 billion yearly come true

If you are still unsatisfied, a full introduction to the Time Value of Money and the Capital Market Pricing Model might
quench your thirst. Spoiler alert, Apple's gigantic size makes it's beta nearly 1

Therefore, with `E = 59 billion` and `r = 8.2%` we have `P = 59 billion / 8.2% = 719.5 billion`

Let's recap:

|                                 |                 |
| ---                             | ---             |
| Cost                            | $879 Billion    |
| Benefits:                       |                 |
| Liquidation Value               | $107 Billion    |
| Future Profits at Current Level | $719.5 Billion  |
| Unexplained                     | $52 Billion     |

What this means is that $719.5 billion of the $772 billion valuation is just assuming Apple Inc. can continue to do what
it's doing today, with no more growth

This still leaves about $52 billion dollars of Apple's $879 billion unexplained. We can now plug this final number by postulating
that this final $52 billion must come from _growth_ in earnings

To make a long story short, this growth can be calculated as:
```
# E = Profit made today (or in the original model, the dividend paid, but we don't have to worry about that for now)
# r = Required rate of return
# g = The rate of profit growth
# P = Value Today
P = D / (r - g) # According to the perpetuity model
# This implies:
g = r - D / P
``` 

Thus, `g = 8.2% - $59 Billion / $772 Billion = 0.55%`. This means despite Apple's meteoric rise and dominance, Wall Street is
collectively expecting Apple Inc. to grow on average less than 1/2 of a percent long-term (well below US GDP growth)

    Apple's Expected Growth Rate According to Our Simple Model is 0.55%  

### Summary of Valuation Exercise

|                                 |                                |
|---                              | ---                            |
| Cost                            | $879 Billion                   |
| Benefits:                       |                                |
| Liquidation Value               | $107 Billion                   |
| Future Profits at Current Level | $719.5 Billion                 |
| Growth of Future Profits        | $52 Billion (growing at 0.55%) |

This is unsurprising, as Apple Inc. have recently become so large and have sold so many smart phones, tablets and devices to 
so many people that it's hard to imagine the company growing fast forever. This low growth rate _expectation_ is indicative
that Wall Street is "doing it's job" of watching and forecasting the large firm that is Apple Inc

Of course, had investor been projecting a growth rate of say 5% instead of 0.55%, Apple's stock prices would be trading at
$390 a share for a market cap of $1.843.75 trillion dollars

On the contrary, if investors believe Apple Inc.' profits will fall below 0.55% long-term, the stock price would decrease as expected

_Note: there is technically a difference between earnings and free-cash-flow (FCF), and for some companies this difference can be quite
substantial in the short to medium term! We are going to ignore those distinctions for now, as a full discussion of all the nuances
would turn this 20 minute reading into a full fledged finance class. The examples here are simplified for illustration. For a full introduction on the topic, several courses of material
and an advanced degree might be unavoidable_

## The Code

Now we begin writing some code

### Data Sources

- Yahoo Finance https://finance.yahoo.com/
- Quandl Fundamental [https://www.quandl.com/databases/SF1]()
- Primary Source SEC https://www.sec.gov/cgi-bin/browse-edgar?action=getcurrent

We will use Yahoo Finance as they have narrowed down and refined the data originally published by the companies themselves with the SEC

If you head over to [AAPL summary page on Yahoo](https://finance.yahoo.com/quote/AAPL?ltr=1), you will see something resembling the page below:

![alt text](https://raw.githubusercontent.com/erfangc/equity-valuation/master/images/aapl-summary.png)

### Using XPath to Navigate Web Pages

To extract tabular data from within a website (with all the advertisements, extra elements and styling). You must learn to use [XPath](https://developer.mozilla.org/en-US/docs/Web/XPath).
XPath is a syntax for succinctly navigating a XML tree, which websites are 

To obtain the XPath to the table with the information we want, right clicking on the table (In Chrome), and then choose "Inspect Element"
 
![alt text](https://raw.githubusercontent.com/erfangc/equity-valuation/master/images/aapl-inspect-element.png)

Navigate the HTML DOM until the entire table is highlighted, which should be the `<table>` element. 
Then right click on the `<table>` element, and choose Copy -> Copy XPath

You may end up with something like the following on your clipboard:
```xpath
//*[@id="quote-summary"]/div[1]/table
```

Save this for the next sections

### Writing a Simple Parser

Next, we begin to write our code to parse the Yahoo Finance pages from above

### Defining Data Classes

For good hygiene and re-usability let's craft some `data class` to house the entities we want to extract. The entity relations
here is analogous to how the data is presented on the website

```kotlin
data class YahooFinance(
    val ticker: String,
    val summary: Summary,
    val financials: List<Financial>,
    val lastUpdated: Instant
) {

    fun latestFinancial(): Financial {
        return financials.sortedByDescending { it.date }.first()
    }

    fun earliestFinancial(): Financial {
        return financials.sortedBy { it.date }.first()
    }

}

data class Summary(
    val ticker: String,
    val name: String,
    val marketCap: Double?,
    val beta3YMonthly: Double?,
    val previousClose: Double?,
    val peRatio: Double?,
    val eps: Double?,
    val industry: String,
    val sector: String
)

data class Financial(
    val date: LocalDate,
    val incomeStatement: Map<String, Double>,
    val balanceSheet: Map<String, Double>,
    val cashflowStatement: Map<String, Double>
) {
    companion object {

        object CashflowStatement {
            const val NetIncome = "NetIncome"
            // skipping for brevity
        }

        object BalanceSheet {
            const val CashAndCashEquivalents = "CashAndCashEquivalents"
            // skipping for brevity
        }

        object IncomeStatement {
            const val TotalRevenue = "TotalRevenue"
            // skipping for brevity
        }
    }
}
```

Note a few things:
 - We use `companion object` constants to express Map keys that represent metrics / field names we extract from the financial 
 statement tables
 - The `Summary` class uses value properties `val` instead of a dynamic Map because we know we are dealing with a very fixed number of 
 metrics
 - Financial statements are extracted into multiple periods, with the latest accessible via the `latestFinancials()` method on the 
 ultimate container class `YahooFinance`
 
 ### SummaryRetriever
 
 The summary page contains information like `beta` and `peRatio`. We use the JSoup library to obtain a `Document` object.
 
 ```kotlin
val summary = Jsoup
            .connect("https://finance.yahoo.com/quote/$ticker")
            .get()
```

Using the XPath we saved from earlier (with some modifications) we can traverse the document using a related
library called XSoup. Note both JSoup and XSoup can be obtained via Maven Repositories

```kotlin
val rows = Xsoup
            .compile("//*[@id=\"quote-summary\"]/div[2]/table/tbody/tr") // note the "tr" at the end, this selects all table rows
            .evaluate(summary)
            .elements
```

At this point, `rows` is just a collection of XML `Element`. To travel each element and find a piece of information
we define the following method `searchTableRows()`:

```kotlin
private fun searchTableRows(trs: Elements, cellToFind: String): Double? {
        return trs
            .find { tr ->
                tr.select("td").first().text() == cellToFind
            }
            ?.select("td")
            ?.last()
            ?.text()
            ?.let { parseDouble(it) }
}
```

Here we assume:
- The 1st cell of every row is the label for a metric
- The last cell of every row is the value

As a result, getting data can be accomplished easily via calls like ones below:

```kotlin
val marketCap = searchTableRows(rows, "Market Cap")
        val beta3YMonthly = searchTableRows(rows, "Beta (3Y Monthly)")
        val peRatio = searchTableRows(rows, "PE Ratio (TTM)")
        val epsTTM = searchTableRows(rows, "EPS (TTM)")
```

### FinancialsRetriever

The class `FinancialsRetriever` uses a similar approach, but these tables represent rows as a give metric, with
each column representing the value for that metric on a given date (or period). 
Therefore we need a slightly more complicated parser

I wrote a helper class called `TableParser` to assist with parsing Yahoo Finance tables where columns are dates and rows represent
analytics. Crucially, we must also account for the fact that some rows are simply separators with no data. This in HTML is expressed
via the `colspan` attribute on the `<tr>` tags

A brief version of the code is shown below:

```kotlin
typealias TableEntity = Map<String, Any>
typealias DatedContent = Map<LocalDate, TableEntity>
```

```kotlin
fun parseTableWithColumnsAsDates(table: Element): DatedContent {
    if (table.tagName() != "table") {
        throw RuntimeException("element must be a table but was ${table.tagName()} instead")
    }
    val rows = table.select("tr")
    val columnToDate = YahooFinanceUtils.columnToDate(rows)
    return rows
        .asSequence()
        // the first row is presumed to label the dates
        .drop(1)
        // skip rows with colspan defined, those usually do not correspond with what we need
        .filter { tr -> tr.attr("colspan").isBlank() }
        .map { tr ->
            val tds = tr.select("td")
            val label = tds.first().text()
            tds
                .drop(1)
                .mapIndexed { idx, td ->
                    val date = columnToDate.getValue(idx)
                    Triple(label, date, td.text())
                }
        }
        .flatten()
        .groupBy { it.second }
        .map { entry ->
            entry.key to entry
                .value
                .map {
                    formatLabel(it.first) to parseDouble(it.third)
                }
                .filter { it.second != null }
                .map { it.first to it.second!! }
                .toMap()
        }
        .toList()
        .toMap()
}
```

Now we have a generic way to parse dated tables, we are ready to create the `FinancialsRetriever` class to parse the three
main accounting statements that companies file with regulators: 
- Balance Sheet: What do I own and owe at a given snapshot in time?
- Income Statement: How much did I earn or loss between snapshots in time?
- Cashflow Statement: Reconciling the changes in cash balances between snapshots, using information from the statements above
 _this is used to spot irregularities and identify fraud_
 
`FinancialsRetriever` has just one method:

```kotlin
fun retrieveFinancials(ticker: String): List<Financial> {
        val incomeStatement = TableParser().parseTableWithColumnsAsDates(getIncomeStatementTable(ticker))
        val cashflowStatement = TableParser().parseTableWithColumnsAsDates(getCashflowStatementTable(ticker))
        val balanceSheet = TableParser().parseTableWithColumnsAsDates(getBalanceSheetTable(ticker))
        return incomeStatement.map { (date, metrics) ->
            Financial(
                date = date,
                incomeStatement = metrics.mapValues { it.value.toString().toDouble() * 1000.0 },
                balanceSheet = balanceSheet.getOrDefault(date, emptyMap()).mapValues { it.value.toString().toDouble() * 1000.0 },
                cashflowStatement = cashflowStatement.getOrDefault(date, emptyMap()).mapValues {
                    it.value.toString().toDouble() * 1000.0
                }
            )
        }
}
```

That's the vast majority of it! Note that once you have a way to retrieve this information for 1 company, you can easily extend
it to many other companies

### ImpliedGrowthRateComputer

Now that we have a way to construct a `YahooFinance` data class, we can create a calculator class to perform the necessary math
to derive the analytics we want to compute. In keeping with the theme of this article, we will compute the implied growth rate of 
companies. Note that `ImpliedGrowthRateComputer` implements the `DerivedComputer` interface, which helps standardize any other 
computers or calculators we might want to create in the future.

```kotlin
interface DerivedComputer {
    fun compute(yahooFinance: YahooFinance, derived: Derived = Derived()): Derived
}
```

We also created the `Derived` data class to distinguish between metrics we compute vs. those we obtained from Yahoo Finance.

```kotlin
class ImpliedGrowthRateComputer : DerivedComputer {

    private val logger = LoggerFactory.getLogger(ImpliedGrowthRateComputer::class.java)

    override fun compute(yahooFinance: YahooFinance, derived: Derived): Derived {
        // calculate implied constant growth rate
        val marketRiskPremium = 0.08
        val summary = yahooFinance.summary
        val ticker = yahooFinance.ticker
        val beta = summary.beta3YMonthly
        return if (beta != null && summary.eps != null && summary.previousClose != null) {
            val r = beta * marketRiskPremium
            // assume EPS ~= FCF
            val D = summary.eps
            val P = summary.previousClose
            val g = r - D / P
            val percentInstance = NumberFormat.getPercentInstance()
            if (g.isNaN()) {
                throw RuntimeException("cannot compute implied constant growth rate for ${summary.ticker} D=$D, r=$r, P=$P, beta=$beta")
            }
            logger.info("$ticker implied constant growth = ${percentInstance.format(g)}, D=$D, r=$r, P=$P, beta=$beta, D/P=${D / P}")
            derived.copy(impliedConstantGrowth = g)
        } else {
            derived
        }
    }
}
```