import re
import random


# The phone number fields in the NPPES data are: `Provider Business Mailing Address Telephone Number`,`Provider Business Mailing Address Fax Number`,`Provider Business Practice Location Address Telephone Number`,`Provider Business Practice Location Address Fax Number`,`Authorized Official Telephone Number`


# the digits are stripped out of number, and joined into a single string
# if number is empty, it is returned
# if there are not 10 digits, then it is an invalid phone number, and a synthetic valid phone number is created
#	with the format (555) 555-####
# else, there is a phone number of 10 digits, and the new_number is constructed of the original's area code,
#	then 555, then a random 4-digit number from 0000 to 9999
# need to import 're' and 'random' for this function
import toolz


@toolz.memoize
def synthetic_number(number):
	digits = ''.join(re.findall(r'\d+', number))
	return digits if not digits else \
		('555' if len(digits) != 10 else digits[:3]) + '555' '{}'.format(random.randint(0,9999))
