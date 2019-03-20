# Where does the stock market think society is headed? (Answered using Kotlin, Elasticsearch)

In this article I try to answer the question, as far as the investment community is concerned,
what is _going to be_ the fastest growing industries in the United States? I will then discuss some implications
of these findings

When investors suddenly believe a company's profit will grow substantially, the company's stock price would increase
regardless of the company's profitability today. This logic is simple enough. 

However, this intuitive relationship between growth and stock price is complicated by the fact that _unpredictability_ of profits also affect
stock prices _today_. In finance, we say this unpredictability give rise to a _risk premium_ or a required discount
to compensate for the aforementioned unpredictability. 
Thus, we must isolate and remove the effect of this risk premium when calculating expected growth. We are going to answer these question using `Kotlin` and scrapping some data from the web

We will introduce the topic and hands-on coding in the following order:

1. Walk through an example of how to compute the growth rate for a single company
2. Show you how to perform the analysis for every company in the United States using Kotlin, Apache and Yahoo Finance
3. Summarize our data using Elasticsearch

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

|   |   |
|---|---|
| Cost | $879 Billion |
| Benefit | $0 |
| Unexplained  | $879 Billion |

Let's break this number down a bit:

The company currently have: 
~$131 billion USD in cash, short-term investments and inventory

This means, if shareholders cleared Apple's bank account, sold all of the stocks & US government bonds Apple owns as well as all
of its excess inventory of iPhones, iPads & MacBooks we would end up with ~$131 billion dollars. These should be relatively
easy to sell and monetize

Further, if Apple divested all of its investments in other companies, patents & longer-term government bonds we would receive another $141.7 billion
in cash

So if we sold every piece of valuable at the company today we'd end up with about $365.7 billion in hard cold cash 
  
Apple currently have debts ~$258 billion (~$116 billion due in the next year). 

Assuming we pay down all of Apple's debt with the $365.7 billion we received from liquidating the company, we will end up
with a net of ~$107 billion dollars on our hands. Therefore, Apple Inc., if it stopped operating _today_ and closed up shop, shareholders
would immediately receive ~107 billion dollars or about ~$22 per share

Recall that the total valuation of Apple is $879.54 billion, of which only $107 billion or roughly just 12% the net wealth
of the company come from the net cash the company have already pocketed.

With that, let's do a progress check:

|    |     |
|--- | --- |
| Cost | $879 Billion |
| Benefits:  |   |
| Liquidation Value | 107 Billion |
| Unexplained| 772 Billion |

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
quench your thirst. Spoiler alert, Apple's beta of 0.99 makes it nearly perfect!

Therefore, with `E = 59 billion` and `r = 8.2%` we have `P = 59 billion / 8.2% = 719.5 billion`

Let's recap:

|    |     |
|--- | --- |
| Cost | $879 Billion |
| Benefits:  |   |
| Liquidation Value | $107 Billion |
| Future Profits at Current Level | $719.5 Billion |
| Unexplained| $52 Billion |

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

|    |     |
|--- | --- |
| Cost | $879 Billion |
| Benefits:  |   |
| Liquidation Value | $107 Billion |
| Future Profits at Current Level | $719.5 Billion |
| Growth of Future Profits| $52 Billion (growing at 0.55%) |

This is unsurprising, as Apple Inc. have recently become so large and have sold so many smart phones, tablets and devices to 
so many people that it's hard to imagine the company growing fast forever. This low growth rate _expectation_ is indicative
that Wall Street is "doing it's job" of watching and forecasting the large firm that is Apple Inc

Of course, had investor been projecting a growth rate of say 5% instead of 0.55%, Apple's stock prices would be trading at
$390 a share for a market cap of $1.843.75 trillion dollars

On the contrary, if investors believe Apple Inc.' profits will fall below 0.55% long-term, the stock price would decrease as expected

## 

_Note: there is technically a difference between earnings and free-cash-flow (FCF), and for some companies this difference can be quite
substantial in the short to medium term! We are going to ignore those distinctions for now, as a full discussion of all the nuances
would turn this 20 minute reading into a full fledged finance class. The examples here are simplified for illustration. For a full introduction on the topic, several courses of material
and an advanced degree might be unavoidable_

## Does this means I should invest in X industry?

Probably not. The numbers presented here represents expectation assuming current valuation of companies is
_fair_

When prices are _fair_ you can never make "extra" money, at least not long-term over repeated investments