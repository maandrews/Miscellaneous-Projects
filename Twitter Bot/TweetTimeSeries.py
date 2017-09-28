# Plot some results from the listening results.
import tweepy
from tweepy import OAuthHandler
import json
import re
import operator
from collections import Counter
from tweepy import Stream
from tweepy.streaming import StreamListener
from nltk.tokenize import word_tokenize
from nltk.corpus import stopwords
import string
from nltk import bigrams
from collections import defaultdict
import pandas

import matplotlib.pyplot as plt
import numpy as np
import math
import glob

blue = '#003399'
orange = '#E66100'

@classmethod
def parse(cls, api, raw):
    status = cls.first_parse(api, raw)
    setattr(status, 'json', json.dumps(raw))
    return status


tweepy.models.Status.first_parse = tweepy.models.Status.parse
tweepy.models.Status.parse = parse

tweepy.models.User.first_parse = tweepy.models.User.parse
tweepy.models.User.parse = parse



punctuation = list(string.punctuation)
stop = stopwords.words('english') + punctuation + ['rt', 'via', 'RT', u'\u2026', '1', '3']


emoticons_str = r"""
    (?:
        [:=;] # Eyes
        [oO\-]? # Nose (optional)
        [D\)\]\(\]/\\OpP] # Mouth
    )"""

regex_str = [
    emoticons_str,
    r'<[^>]+>', # tags
    r'(?:@[\w_]+)', # mentions
    r"(?:\#+[\w_]+[\w\'_\-]*[\w_]+)", # hash-tags
    r'http[s]?://(?:[a-z]|[0-9]|[$-_@.&+]|[!*\(\),]|(?:%[0-9a-f][0-9a-f]))+', # URLs

    r'(?:(?:\d+,?)+(?:\.?\d+)?)', # numbers
    r"(?:[a-z][a-z'\-_]+[a-z])", # words with - and '
    r'(?:[\w_]+)', # other words
    r'(?:\S)' # else
]

tokens_re = re.compile(r'('+'|'.join(regex_str)+')', re.VERBOSE | re.IGNORECASE)
emoticon_re = re.compile(r'^'+emoticons_str+'$', re.VERBOSE | re.IGNORECASE)
#
def tokenize(s):
    return tokens_re.findall(s)
#
def preprocess(s, lowercase=False):
    tokens = tokenize(s)
    if lowercase:
        tokens = [token if emoticon_re.search(token) else token.lower() for token in tokens]
    return tokens

fname = 'BlueJays.json'

dates_jaystag = []
dates_price = []

# f is the file pointer to the JSON data set
with open('BlueJays.json', 'r') as f:
    for line in f:
        tweet = json.loads(line)
        # let's focus on hashtags only at the moment
        terms_hash = [term for term in preprocess(tweet['text']) if term.startswith('#')]
        # track when the hashtag is mentioned
        if '#BlueJays' in terms_hash:
            dates_jaystag.append(tweet['created_at'])

with open('BlueJays.json', 'r') as f:
    for line in f:
        tweet = json.loads(line)
        # let's focus on hashtags only at the moment
        terms_hash = [term for term in preprocess(tweet['text']) if term.startswith('@')]
        # track when the hashtag is mentioned
        if '@DAVIDprice14' in terms_hash:
            dates_price.append(tweet['created_at'])


# a list of "1" to count the hashtags
ones = [1]*len(dates_jaystag)
# the index of the series
idx = pandas.DatetimeIndex(dates_jaystag)
# the actual series
jaystag = pandas.Series(ones, index=idx)
# Resampling / bucketing
per_minute = jaystag.resample('1Min', how='sum').fillna(0)

# a list of "1" to count the hashtags
ones2 = [1]*len(dates_price)
# the index of the series
idx2 = pandas.DatetimeIndex(dates_price)
# the actual series
pricetag = pandas.Series(ones2, index=idx2)
# Resampling / bucketing
per_minute2 = pricetag.resample('1Min', how='sum').fillna(0)


plt.plot(per_minute, color=blue, linewidth=2, label='#BlueJays', linestyle='-')
plt.plot(per_minute2, color=orange, linewidth=2, label='@DAVIDprice14', linestyle='--')
plt.xlabel('Time')
plt.ylabel('Number of Tweets')
plt.xticks([0, 30, 60, 90, 120, 150], ['1:07', '1:37', '2:07', '2:37', '3:07','3:37'])
plt.legend(fontsize='medium', loc='upper right')
plt.show()
