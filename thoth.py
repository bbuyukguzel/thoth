__author__ = 'Batuhan Buyukguzel'

#TODO source metodunda exception olmali, yoksa program kesintiye ugrayacak


import re
import urllib
import requests
import json
from bs4 import BeautifulSoup



class Process():
    pass

class GetFeed():
    def __init__(self, title, url, summary=None):
        self.title = title
        self.url = url
        self.summary = summary
        self.point = {'strong sell': -2, 'sell': -1, 'neutral': 0, 'buy': 1, 'strong buy': 2}

        if (title == 'barchart'):
            self.summary = self.barchart()
        elif (title == 'forexcorporate'):
            self.summary = self.forexcorporate()
        elif (title == 'fxempire'):
            self.summary = self.fxempire()
        elif (title == 'investing'):
            self.summary = self.investing()


    def source(self):
        user_agent = 'Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.0.7) Gecko/2009021910 Firefox/3.0.7'
        headers = {'User-Agent': user_agent, }
        request = urllib.request.Request(self.url, None, headers)
        response = urllib.request.urlopen(request)
        return response.read()


    def price(self):
        soup = BeautifulSoup(self.source())
        dollar = (soup.find(id='last_last').get_text()).replace(',','')
        return float(dollar)


    def barchart(self):
        pattern = "Short Term Indicators Average:&nbsp;<span class=(.*?)</span>"
        full_data = bytes.decode(self.source())
        half_data = re.findall(pattern, full_data)
        final_data = re.findall(r'[0-9].*', half_data[0])
        percent, summary = final_data[0].split("% ")
        percent = int(percent)
        # ####################
        if (50 <= percent <= 60):
            return 1 if summary == "Buy" else -1
        elif (percent >= 80):
            return 2 if summary == "Buy" else -2
        else:
            return 0


    def forexcorporate(self):
        data = bytes.decode(self.source())
        data = json.loads(data)
        signal = data['trendtext']
        # ####################
        if (signal == 'Strong Buy') | (signal == 'Buy'):
            return 2 if len(signal) == 10 else 1
        elif (signal == 'Neutral'):
            return 0
        else:
            return -2 if len(signal) == 11 else -1


    def fxempire(self):
        pattern_keys = "<div class=\"label gray bold txt_center\">(.*?)</div>"
        pattern_values = "<div class=\"bold txt_center action(.*?)>"
        data = bytes.decode(self.source())
        keys = re.findall(pattern_keys, data)
        values = re.findall(pattern_values, data)
        values = [i.replace(" act_", "").replace("\"", "") for i in values]
        dictionary = dict(zip(keys, values))
        # ####################
        if (dictionary["Summary"] == "No Clear Signal"):
            return 0
        elif (dictionary["Summary"] == "Buy"):
            return 1.25
        else:
            return -1.25



    def investing(self): #5 hour
        soup = BeautifulSoup(self.source())
        table = soup.find(id="techStudiesInnerBoxRightBottom")
        summary = table.find_all('span')[0].get_text()
        maverages = table.find_all('span')[4].get_text()
        tindicators = table.find_all('span')[12].get_text()
        if (summary == 'STRONG SELL') | (summary == 'SELL'):
            return -2 if len(summary) == 11 else -1
        elif (summary == 'NEUTRAL'):
            return 0
        else:
            return 2 if len(summary) == 10 else 1



def main():

    urls = {'barchart': 'http://www.barchart.com/opinions/forex/%5EXAUUSD',
            'forexcorporate': 'http://forexcorporate.com/lib/quotebox/getfinance.php?lg=en&currency=Gold',
            'fxempire': 'http://www.fxempire.com/wp-content/themes/fxempire/ajax_content.php?param=4_hours&action=get_w_c_ts_tab&_nonce=d78349ba82&ajax_params=term_slug%3Dgold',
            'investing': 'http://www.investing.com/commodities/gold-technical?period=18000'
    }

    print(GetFeed(None,'http://www.investing.com/commodities/gold-technical').price())

    """
    for k, v in urls.items():
        print(k, GetFeed(k, v).summary)
"""


if __name__ == '__main__':
    main()