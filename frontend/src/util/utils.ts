
export const formatDateTime = (dateInput: string): string => {

    const date = new Date(dateInput);
    const day = date.getDate().toString().padStart(2, '0');
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const year = date.getFullYear();
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    const seconds = date.getSeconds().toString().padStart(2, '0');


    const formattedDate = `${day}/${month}/${year} ${hours}:${minutes}:${seconds}`;

    return formattedDate
}


export const getParamStr = (keyword: string = "", page: number = 0, size: number = 4, orderBy: string = "", orderDirection: string = ""): string => {
    let sort = orderBy;
    if (orderDirection.length > 0) {
        sort += `,${orderDirection}`;
    }
    return `keyword=${keyword}&page=${page}&size=${size}&sort=${sort}`;
}
