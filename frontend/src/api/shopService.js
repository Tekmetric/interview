import { axiosPrivate } from "api/axiosApi";
import { userPersistence } from "helpers/userPersistence";

export const shopService = {
  search: (search, page) => {
    let url = "/shops?";

    if (search) {
      url += "search=" + search;
    }

    if (page) {
      url += "&page=" + page;
    }

    return axiosPrivate.get(url, {
      headers: {
        "Content-Type": "application/json",
        Authorization: "Bearer " + userPersistence.accessToken(),
      },
      withCredentials: true,
    });
  },
};
