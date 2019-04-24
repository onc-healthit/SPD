import csv
from functools import wraps


def load_csv(file, delimiter=',', skip_headers=True):
    """
    Loads a csv file in memory
    :return: A list of rows
    """
    with open(file, newline='') as f:
        reader = csv.reader(f, delimiter=delimiter)
        if skip_headers:
            _ = next(reader)
        return [_ for _ in reader]
