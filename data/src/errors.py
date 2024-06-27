class DataCollectorBaseError(Exception):
    pass


class DataFetchRequestError(DataCollectorBaseError):
    pass


class DataHeaderError(DataCollectorBaseError):
    pass
