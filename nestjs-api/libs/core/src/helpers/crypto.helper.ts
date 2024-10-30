import * as bcrypt from 'bcrypt';

class CryptoHelperClass {
  private static instance: CryptoHelperClass;

  private algorithm = 'aes-256-ctr';
  private iv: Buffer;

  private constructor() {
    this.iv = Buffer.alloc(16);
  }
  public static getInstance() {
    if (!CryptoHelperClass.instance) {
      CryptoHelperClass.instance = new CryptoHelperClass();
    }
    return CryptoHelperClass.instance;
  }

  public hash(word: string): string {
    const saltRounds = 10;

    return bcrypt.hashSync(word, saltRounds);
  }

  public compare(word: string, hash: string) {
    return bcrypt.compareSync(word, hash);
  }

  public btoa(text: string) {
    return Buffer.from(text, 'binary').toString('base64');
  }

  public atob(base64: string) {
    return Buffer.from(base64, 'base64').toString('binary');
  }
}

const CryptoHelper = CryptoHelperClass.getInstance();
export default CryptoHelper;
