import csv
import json
from collections import defaultdict


def csv_to_json(csv_filename, json_filename):
    # data structures init
    data_list = []
    current_id = None
    current_group = None

    # read csv
    with open(csv_filename, 'r', encoding='utf-8-sig') as csvfile:
        reader = csv.DictReader(csvfile)

        for row in reader:
            # checking Detailed Item ID
            if row['Detailed Item ID  ↓'] and row['Detailed Item ID  ↓'].startswith('DITM-'):
                # If insdie current group
                if current_group:
                    data_list.append(current_group)

                # create new Item group
                current_id = row['Detailed Item ID  ↓']
                current_group = {
                    "Detailed Item ID": current_id,
                    "Item Name": row['Item Name'],
                    "Item ID": row['Item ID'],
                    "Detailed Item Name": row['Detailed Item Name'],
                    "Sector Mapping ID": row['Sector Mapping ID'],
                    "Sector Name": row['Sector Name'],
                    "Subtotal": 0,  # init to 0
                    "Organizations": []
                }

            # process Subtotal
            elif row['Detailed Item ID  ↓'] == 'Subtotal':
                if current_group:
                    # get count from Item Name column
                    try:
                        current_group["Subtotal"] = int(row['Item Name'])
                    except (ValueError, TypeError):
                        current_group["Subtotal"] = 0

            # add organisations
            elif current_id and row['Detailed Item ID  ↓'] == '':
                entry = {key: value for key, value in row.items()
                         if key not in ['', 'Detailed Item ID  ↓', 'Item Name', 'Item ID',
                                        'Detailed Item Name', 'Sector Mapping ID', 'Sector Name']}
                entry['Organisation: Organisation Name'] = 'Organisation Name'
                current_group["Organizations"].append(entry)

    # add last group
    if current_group:
        data_list.append(current_group)

    # write into json
    with open(json_filename, 'w', encoding='utf-8') as jsonfile:
        json.dump(data_list, jsonfile, indent=2, ensure_ascii=False)

    return data_list


# main
if __name__ == "__main__":
    # csv file name, compatible with .csv directly exported from excel
    csv_filename = "20250827 - Capability Data Extract for Navigator.csv"
    json_filename = "output.json"

    result = csv_to_json(csv_filename, json_filename)
    print(f"transformation finished！{len(result)} Detailed Item ID processed")