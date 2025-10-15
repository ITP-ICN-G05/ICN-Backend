import csv
import json
from collections import defaultdict


def csv_to_json(csv_filename, items_json_filename, orgs_json_filename):
    # 初始化数据结构
    items_list = []  # 存储所有项目
    organisations_dict = defaultdict(dict)  # 存储所有组织，以组织ID为键
    current_item_id = None
    current_item = None

    # 读取CSV文件
    with open(csv_filename, 'r', encoding='utf-8-sig') as csvfile:
        reader = csv.DictReader(csvfile)

        for row in reader:
            # 检查是否是新的Detailed Item
            if row['Detailed Item ID  ↓'] and row['Detailed Item ID  ↓'].startswith('DITM-'):
                # 如果当前有正在处理的项目，先保存
                if current_item:
                    items_list.append(current_item)

                # 创建新的项目
                current_item_id = row['Detailed Item ID  ↓']
                current_item = {
                    "Detailed Item ID": current_item_id,
                    "Item Name": row['Item Name'],
                    "Item ID": row['Item ID'],
                    "Detailed Item Name": row['Detailed Item Name'],
                    "Sector Mapping ID": row['Sector Mapping ID'],
                    "Sector Name": row['Sector Name'],
                    "Subtotal": 0,  # 初始化为0
                    "Organisation IDs": []  # 只存储组织ID，不存储完整组织信息
                }

            # 处理小计行
            elif row['Detailed Item ID  ↓'] == 'Subtotal':
                if current_item:
                    # 从Item Name列获取计数
                    try:
                        current_item["Subtotal"] = int(row['Item Name'])
                    except (ValueError, TypeError):
                        current_item["Subtotal"] = 0

            # 处理组织行
            elif current_item_id and row['Detailed Item ID  ↓'] == '':
                # 提取组织信息
                org_id = row['Organisation: Organisation ID']
                
                # 如果有有效的组织ID
                if org_id:
                    # 创建组织与项目关联的详细信息
                    item_org_relationship = {
                        "Detailed Item ID": current_item_id,
                        "Organisation Capability": row['Organisation Capability'],
                        "Capability Type": row['Capability Type'],
                        "Validation Date": row['Validation Date']
                    }
                    
                    # 如果该组织尚未在字典中，则添加基本信息
                    if org_id not in organisations_dict:
                        organisations_dict[org_id] = {
                            "Organisation ID": org_id,
                            "Organisation Name": row['Organisation: Organisation Name'],
                            "Billing Street": row['Organisation: Billing Street'],
                            "Billing City": row['Organisation: Billing City'],
                            "Billing State/Province": row['Organisation: Billing State/Province'],
                            "Billing Zip/Postal Code": row['Organisation: Billing Zip/Postal Code'],
                            "Associated Items": []  # 存储与每个项目关联的详细信息
                        }
                    
                    # 将项目关联信息添加到组织的关联项目列表中
                    organisations_dict[org_id]["Associated Items"].append(item_org_relationship)
                    
                    # 将组织ID添加到当前项目的组织ID列表中（如果尚未存在）
                    if org_id not in current_item["Organisation IDs"]:
                        current_item["Organisation IDs"].append(org_id)

    # 添加最后一个项目
    if current_item:
        items_list.append(current_item)

    # 写入项目JSON文件
    with open(items_json_filename, 'w', encoding='utf-8') as jsonfile:
        json.dump(items_list, jsonfile, indent=2, ensure_ascii=False)

    # 准备组织数据列表
    organisations_list = []
    for org_id, org_data in organisations_dict.items():
        # 计算每个组织关联的项目数量
        org_data['Associated Item Count'] = len(org_data['Associated Items'])
        organisations_list.append(org_data)

    # 写入组织JSON文件
    with open(orgs_json_filename, 'w', encoding='utf-8') as org_jsonfile:
        json.dump(organisations_list, org_jsonfile, indent=2, ensure_ascii=False)

    return items_list, organisations_list


# 主程序
if __name__ == "__main__":
    # CSV文件名，兼容直接从Excel导出的.csv文件
    csv_filename = "20250827 - Capability Data Extract for Navigator.csv"
    items_json_filename = "items.json"
    orgs_json_filename = "organisations.json"

    items, organisations = csv_to_json(csv_filename, items_json_filename, orgs_json_filename)
    print(f"转换完成！处理了 {len(items)} 个详细项目")
    print(f"组织文件已创建！找到 {len(organisations)} 个独立组织")