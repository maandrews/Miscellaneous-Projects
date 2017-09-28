# Listen to tweets with specific key words/ phrases.
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




consumer_key = 'NCqKtAbivgRU279ExkotrD3HX'
consumer_secret = 'cg55WPh3bRzBLvN7KlCR12be6yeLMnW9BEoybV7rDY5BXLtdKe'
access_token = '3399277288-dHK8Ys1o5XogMloYgjZnUFOmWGYPQsncdbiSwqC'
access_secret = 'xquqXiyN3YAgS45QbJ4g2boq6AAk7xnQj25ryvT9vgPRe'

auth = OAuthHandler(consumer_key, consumer_secret)
auth.set_access_token(access_token, access_secret)

api = tweepy.API(auth)

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
    r'<[^>]+>', # HTML
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

def tokenize(s):
    return tokens_re.findall(s)

def preprocess(s, lowercase=False):
    tokens = tokenize(s)
    if lowercase:
        tokens = [token if emoticon_re.search(token) else token.lower() for token in tokens]
    return tokens

fname = 'BlueJays.json'
with open(fname, 'r') as f:
     count_all = Counter()
     for line in f:
         tweet = json.loads(line)
         # Create a list with all the terms
         terms_stop = [term for term in preprocess(tweet['text']) if term not in stop]
         # Common word tuples
         terms_bigram = bigrams(terms_stop)
         # Count terms only once, equivalent to Document Frequency
         terms_single = set(terms_stop)
         # Count hashtags only
         terms_hash = [term for term in preprocess(tweet['text']) if term.startswith('#')]
         # Count terms only (no hashtags, no mentions)
         terms_only = [term for term in preprocess(tweet['text']) if term not in stop and not term.startswith(('#', '@'))]
         # Update the counter
         count_all.update(terms_stop)
     # Print the first 5 most frequent words
     print(count_all.most_common(5))


# Printing the file
# with open('BlueJays.json', 'r') as f:
#     for line in f:
#         tweet = json.loads(line)
#         tokens = preprocess(tweet['text'])
#         print(tokens)


# Listen to tweets real-time
class MyListener(StreamListener):

     def on_data(self, data):
         try:
             with open('BlueJays.json', 'a') as f:
                 f.write(data)
                 return True
         except BaseException as e:
             print("Error on_data: %s" % str(e))
         return True

     def on_error(self, status):
         print(status)
         return True

twitter_stream = Stream(auth, MyListener())
twitter_stream.filter(track=['#BlueJays','#bluejays'])
