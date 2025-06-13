import api from '.';

const SERVICE_URL = 'http://localhost:8000/api';

function getResources({
	filter,
	limit,
	offset,
	sortDirection,
	sortColumn,
}: {
	filter: string;
	limit: number;
	offset: number;
	sortDirection: 'asc' | 'desc';
	sortColumn?: string;
}) {
	return api.get('/resources', {
		baseURL: SERVICE_URL,
		params: {
			filter,
			limit,
			offset,
			sortDirection,
			sortColumn,
		},
	});
}

function addResource(data: any) {
	return api.post('/resources', {
		baseURL: SERVICE_URL,
		data: {
			resource: data,
		},
	});
}

function updateResource(id: string, data: any) {
	return api.put(`/resources/${id}`, {
		baseURL: SERVICE_URL,
		data: {
			resource: data,
		},
	});
}

function deleteResource(id: string) {
	return api.delete(`/resources/one/${id}`, {
		baseURL: SERVICE_URL,
	});
}

function bulkDeleteResources(ids: string[]) {
	return api.delete('/resources/bulk', {
		baseURL: SERVICE_URL,
		data: { ids },
	});
}

export {
	getResources,
	addResource,
	updateResource,
	deleteResource,
	bulkDeleteResources,
};
