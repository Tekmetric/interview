from datetime import datetime
import pandas as pd
from tqdm import tqdm

def process_data(data):
    records = []
    for item in tqdm(data['data'], desc="Processing Data"):
        record = {
            "Id": item[0],
            "created_at": datetime.fromtimestamp(item[3]),
            "updated_at": datetime.fromtimestamp(item[5]),
            "report_received_date": item[8],
            "nhtsa_id": item[9],
            "recall_link": item[10][0], 
            "manufacturer": item[11],
            "subject": item[12],
            "component": item[13],
            "mfr_campaign_number": item[14],
            "recall_type": item[15],
            "potentially_affected": item[16],
            "defect_summary": item[17],
            "consequence_summary": item[18],
            "corrective_action": item[19],
            "fire_risk_when_parked": item[20],
            "do_not_drive": item[21],
            "completion_rate": item[22]
        }
        records.append(record)
    
    df = pd.DataFrame(records)
    df['report_received_date'] = pd.to_datetime(df['report_received_date'])
    return df
