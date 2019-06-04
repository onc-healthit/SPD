import csv
import os

import toolz


@toolz.curry
def load_vocabulary(vocab_type, file):
    """
    Loads a csv file in memory
    :return: A list of rows
    """
    path = os.path.join(os.path.dirname(__file__), '..', 'vocabulary', vocab_type, file)
    with open(path, newline='') as f:
        reader = csv.reader(f)
        return set(_[0] for _ in reader)
