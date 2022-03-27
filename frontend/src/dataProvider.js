import {fetchUtils} from 'react-admin';
import {stringify} from 'query-string';

const apiUrl = 'http://localhost:8080/api';
const httpClient = fetchUtils.fetchJson;

export default {
    getList: (resource, params) => {
        const {page, perPage} = params.pagination;
        const {field, order} = params.sort;
        const filters = params.filter

        const query = {
            size: perPage,
            page: page - 1,
            field,
            order,
            partName: filters.q,
            partNumber: filters.partNumber,
            brand: filters.brand,
            supportEmail: filters.supportEmail,
            status: filters.status
        };
        const url = `${apiUrl}/${resource}?${stringify(query)}`;

        return httpClient(url).then(({headers, json}) => {
            return {
                data: json.content,
                total: json.totalElements
            }
        });
    },

    getOne: (resource, params) =>
        httpClient(`${apiUrl}/${resource}/${params.id}`).then(({json}) => ({
            data: json,
        })),

    getMany: (resource, params) => {
        const query = {
            filter: JSON.stringify({id: params.ids}),
        };
        const url = `${apiUrl}/${resource}?${stringify(query)}`;
        return httpClient(url).then(({json}) => ({data: json}));
    },

    update: (resource, params) =>
        httpClient(`${apiUrl}/${resource}/${params.id}`, {
            method: 'PUT',
            body: JSON.stringify(params.data),
        }).then(({json}) => ({data: json})),

    create: (resource, params) =>
        httpClient(`${apiUrl}/${resource}`, {
            method: 'POST',
            body: JSON.stringify(params.data),
        }).then(({json}) => ({
            data: {...params.data, id: json.id},
        })),

    delete: (resource, params) =>
        httpClient(`${apiUrl}/${resource}/${params.id}`, {
            method: 'DELETE',
        }).then(() => ({data: {id: params.id}})),

    deleteMany: (resource, params) => {
        const query = {
            ids: params.ids.join(","),
        };
        return httpClient(`${apiUrl}/${resource}?${stringify(query)}`, {
            method: 'DELETE',
        }).then(() => ({data: params.ids}));
    }
};
