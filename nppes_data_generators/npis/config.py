'''
Real NPIs are made of ten digits and start with either 1 or 2.
In order to avoid using any potential real NPI we'll start our synthetic NPIs from 999999999 (i.e. 10**9-1) downward
and we'll stop at the last one before 299999999 which are the first nine digits of the highest possible real NPI i.e.
we'll stop at 300000000 (i.e. 3*10**8).

Also as per the official documentation (https://tinyurl.com/y6hab2vk):
"...in order that any NPI could be used as a card issuer identifier on a standard health identification card, the check
digit will always be calculated as if the prefix (80840) is present.  This is accomplished by adding the constant 24 in
step 2 of the check digit calculation"
'''

LOWER_LIMIT = 3*10**8

UPPER_LIMIT = 10**9-1

PREFIX = 24
