import csv
import json
from collections import defaultdict


def csv_to_json(csv_filename, items_json_filename, orgs_json_filename):

    items_list = []
    organisations_dict = defaultdict(dict)
    current_item_id = None
    current_item = None

    with open(csv_filename, 'r', encoding='utf-8-sig') as csvfile:
        reader = csv.DictReader(csvfile)

        for row in reader:
            if row['Detailed Item ID  ↓'] and row['Detailed Item ID  ↓'].startswith('DITM-'):
                if current_item:
                    items_list.append(current_item)

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

            elif row['Detailed Item ID  ↓'] == 'Subtotal':
                if current_item:
                    try:
                        current_item["Subtotal"] = int(row['Item Name'])
                    except (ValueError, TypeError):
                        current_item["Subtotal"] = 0

            elif current_item_id and row['Detailed Item ID  ↓'] == '':
                org_id = row['Organisation: Organisation ID']
                
                if org_id:
                    item_org_relationship = {
                        "Detailed Item ID": current_item_id,
                        "Organisation Capability": row['Organisation Capability'],
                        "Capability Type": row['Capability Type'],
                        "Validation Date": row['Validation Date']
                    }
                    
                    if org_id not in organisations_dict:
                        organisations_dict[org_id] = {
                            "Organisation ID": org_id,
                            "Organisation Name": row['Organisation: Organisation Name'],
                            "Billing Street": row['Organisation: Billing Street'],
                            "Billing City": row['Organisation: Billing City'],
                            "Billing State/Province": row['Organisation: Billing State/Province'],
                            "Billing Zip/Postal Code": row['Organisation: Billing Zip/Postal Code'],
                            "Associated Items": []
                        }
                    
                    organisations_dict[org_id]["Associated Items"].append(item_org_relationship)
                    
                    if org_id not in current_item["Organisation IDs"]:
                        current_item["Organisation IDs"].append(org_id)

    if current_item:
        items_list.append(current_item)

    with open(items_json_filename, 'w', encoding='utf-8') as jsonfile:
        json.dump(items_list, jsonfile, indent=2, ensure_ascii=False)

    organisations_list = []
    for org_id, org_data in organisations_dict.items():
        org_data['Associated Item Count'] = len(org_data['Associated Items'])
        organisations_list.append(org_data)

    with open(orgs_json_filename, 'w', encoding='utf-8') as org_jsonfile:
        json.dump(organisations_list, org_jsonfile, indent=2, ensure_ascii=False)

    return items_list, organisations_list


if __name__ == "__main__":
    csv_filename = "20250827 - Capability Data Extract for Navigator.csv"
    items_json_filename = "items.json"
    orgs_json_filename = "organisations.json"

    items, organisations = csv_to_json(csv_filename, items_json_filename, orgs_json_filename)
    print(f"finished {len(items)} record")
    print(f"file created {len(organisations)} company")