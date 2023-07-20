export type FormDataType = {
  client_id: string;
  client_secret: string;
};

export type LoginDataType = FormDataType & {
  grant_type: string;
  scope: string;
};

export type AuthDataType = {
  [key: string]: unknown;
  access_token: string;
};
