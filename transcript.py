import csv
import json
from collections import defaultdict
import os


def csv_to_json(csv_filename, items_json_filename, orgs_json_filename):
    items_list = []
    organisations_dict = defaultdict(dict)
    current_item_id = None
    current_item = None

    with open(csv_filename, 'r', encoding='utf-8-sig') as csvfile:
        reader = csv.DictReader(csvfile)

        for row in reader:
            # check new Detailed Item 
            if row['Detailed Item ID  ↓'] and row['Detailed Item ID  ↓'].startswith('DITM-'):
                # stop last item
                if current_item:
                    items_list.append(current_item)

                # create new
                current_item_id = row['Detailed Item ID  ↓']
                current_item = {
                    "Detailed Item ID": current_item_id,
                    "Item Name": row['Item Name'],
                    "Item ID": row['Item ID'],
                    "Detailed Item Name": row['Detailed Item Name'],
                    "Sector Mapping ID": row['Sector Mapping ID'],
                    "Sector Name": row['Sector Name'],
                    "Subtotal": 0, 
                    "Organisation IDs": [] 
                }

            # process subtotals
            elif row['Detailed Item ID  ↓'] == 'Subtotal':
                if current_item:
                    # read from Item Name column
                    try:
                        current_item["Subtotal"] = int(row['Item Name'])
                    except (ValueError, TypeError):
                        current_item["Subtotal"] = 0

            # process organisations
            elif current_item_id and row['Detailed Item ID  ↓'] == '':
                org_info = {}
                org_id = None
                
                for key, value in row.items():
                    if key and key not in ['', 'Detailed Item ID  ↓', 'Item Name', 'Item ID',
                                          'Detailed Item Name', 'Sector Mapping ID', 'Sector Name']:
                        org_info[key] = value
                        
                        # extract ID
                        if 'Organisation ID' in key and value:
                            org_id = value
                
                if org_id:
                    # add is not exist
                    if org_id not in organisations_dict:
                        organisations_dict[org_id] = org_info
                        organisations_dict[org_id]['Associated Detailed Item IDs'] = []
                    
                    if current_item_id not in organisations_dict[org_id]['Associated Detailed Item IDs']:
                        organisations_dict[org_id]['Associated Detailed Item IDs'].append(current_item_id)
                    
                    if org_id not in current_item["Organisation IDs"]:
                        current_item["Organisation IDs"].append(org_id)

    # add last
    if current_item:
        items_list.append(current_item)

    # write item file
    with open(items_json_filename, 'w', encoding='utf-8') as jsonfile:
        json.dump(items_list, jsonfile, indent=2, ensure_ascii=False)

    # organisation
    organisations_list = []
    for org_id, org_data in organisations_dict.items():
        # calculate count
        org_data['Associated Item Count'] = len(org_data['Associated Detailed Item IDs'])
        organisations_list.append(org_data)

    # write organisation file
    with open(orgs_json_filename, 'w', encoding='utf-8') as org_jsonfile:
        json.dump(organisations_list, org_jsonfile, indent=2, ensure_ascii=False)

    return items_list, organisations_list


# main
if __name__ == "__main__":
    # CSV files，compatible with csv exported from Excel
    csv_filename = "20250827 - Capability Data Extract for Navigator.csv"
    items_json_filename = "items.json"
    orgs_json_filename = "organisations.json"

    items, organisations = csv_to_json(csv_filename, items_json_filename, orgs_json_filename)
    print(f"Transformation finished! {len(items)} Detailed Item IDs processed")
    print(f"Organisations file created! {len(organisations)} unique organisations found")