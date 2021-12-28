
def check_missing_data(data):
    missing_data = ""

    data_is_missing = False
    for key in data:
        if data[key] is None:
            data_is_missing = True
            missing_data += f'{key}, '

    missing_data = missing_data[:-2]  # remove ', ' at the end

    return data_is_missing, missing_data
