import * as moment from 'moment';

export const DEFAULT_DAY_FORMAT = 'd';
export const DEFAULT_MINUTE_FORMAT = 'm';
export const DEFAULT_DATE_FORMAT = 'YYYY-MM-DD';
export const DEFAULT_TIME_FORMAT = 'HH:mm:ss';

class DateHelperClass {
  private static instance: DateHelperClass;

  private constructor() {
    //
  }

  public static getInstance() {
    if (!DateHelperClass.instance) {
      DateHelperClass.instance = new DateHelperClass();
    }
    return DateHelperClass.instance;
  }

  public addDays(value: any) {
    return moment().add(value, DEFAULT_DAY_FORMAT).toDate();
  }
}

const DateHelper = DateHelperClass.getInstance();

export default DateHelper;
