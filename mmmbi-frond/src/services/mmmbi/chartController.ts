// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** addChartEntity POST /api/chart/add */
export async function addChartEntityUsingPost(
  body: API.ChartAddRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseLong_>('/api/chart/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** deleteChartEntity POST /api/chart/delete/do */
export async function deleteChartEntityUsingPost(
  body: API.DeleteRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseBoolean_>('/api/chart/delete/do', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** deleteChartDocument POST /api/chart/delete/doc */
export async function deleteChartDocumentUsingPost(
  body: API.DeleteChartDocRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseBoolean_>('/api/chart/delete/doc', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** editChartEntity POST /api/chart/edit */
export async function editChartEntityUsingPost(
  body: API.ChartEditRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseBoolean_>('/api/chart/edit', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** genChartByAi POST /api/chart/gen/async/mq */
export async function genChartByAiUsingPost(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.genChartByAiUsingPOSTParams,
  body: {},
  file?: File,
  options?: { [key: string]: any },
) {
  const formData = new FormData();

  if (file) {
    formData.append('file', file);
  }

  Object.keys(body).forEach((ele) => {
    const item = (body as any)[ele];

    if (item !== undefined && item !== null) {
      if (typeof item === 'object' && !(item instanceof File)) {
        if (item instanceof Array) {
          item.forEach((f) => formData.append(ele, f || ''));
        } else {
          formData.append(ele, JSON.stringify(item));
        }
      } else {
        formData.append(ele, item);
      }
    }
  });

  return request<API.BaseResponseBiResponse_>('/api/chart/gen/async/mq', {
    method: 'POST',
    params: {
      ...params,
    },
    data: formData,
    requestType: 'form',
    ...(options || {}),
  });
}

/** getChartEntityById GET /api/chart/get */
export async function getChartEntityByIdUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getChartEntityByIdUsingGETParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseChartGen_>('/api/chart/get', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** getAllCharts POST /api/chart/list/chart/all */
export async function getAllChartsUsingPost(
  body: API.ChartQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponsePage_>('/api/chart/list/chart/all', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** getUnsucceedChart POST /api/chart/list/chart/unsucceed */
export async function getUnsucceedChartUsingPost(
  body: API.ChartQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponsePage_>('/api/chart/list/chart/unsucceed', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** listChartEntityByPage POST /api/chart/list/page */
export async function listChartEntityByPageUsingPost(
  body: API.ChartQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponsePageChartGen_>('/api/chart/list/page', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** listMyChartEntityByPage POST /api/chart/my/list/page */
export async function listMyChartEntityByPageUsingPost(
  body: API.ChartQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponsePageChartGen_>('/api/chart/my/list/page', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** regenerateChart GET /api/chart/regen/chart */
export async function regenerateChartUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.regenerateChartUsingGETParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseString_>('/api/chart/regen/chart', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** updateChartEntity POST /api/chart/update */
export async function updateChartEntityUsingPost(
  body: API.ChartUpdateRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseBoolean_>('/api/chart/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
