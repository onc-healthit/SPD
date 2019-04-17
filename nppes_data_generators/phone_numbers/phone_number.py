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
def synthetic_number (number):
	digits = ''.join(re.findall('\d+',number))
	if(len(number) == 0):
		return number
	if(len(digits) != 10):
		new_number = []
		new_number.append('555555')
		new_number.append("%04d" % random.randint(0,9999))
		return ''.join(new_number)
	else:
		new_number = []
		new_number.append(digits[0:3])
		new_number.append('555')
		new_number.append("%04d" % random.randint(0,9999))
		return ''.join(new_number)
