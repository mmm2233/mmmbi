// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** addTextTask POST /api/text/add */
export async function addTextTaskUsingPost(
  body: API.TextAddRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseLong_>('/api/text/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** deleteTextTask POST /api/text/delete */
export async function deleteTextTaskUsingPost(
  body: API.DeleteRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseBoolean_>('/api/text/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** editTextTask POST /api/text/edit */
export async function editTextTaskUsingPost(
  body: API.TextEditRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseBoolean_>('/api/text/edit', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** genTextTaskAi POST /api/text/gen */
export async function genTextTaskAiUsingPost(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.genTextTaskAiUsingPOSTParams,
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

  return request<API.BaseResponseAiResponse_>('/api/text/gen', {
    method: 'POST',
    params: {
      ...params,
    },
    data: formData,
    requestType: 'form',
    ...(options || {}),
  });
}

/** genTextTaskAsyncAiMq POST /api/text/gen/async/mq */
export async function genTextTaskAsyncAiMqUsingPost(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.genTextTaskAsyncAiMqUsingPOSTParams,
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

  return request<API.BaseResponseAiResponse_>('/api/text/gen/async/mq', {
    method: 'POST',
    params: {
      ...params,
    },
    data: formData,
    requestType: 'form',
    ...(options || {}),
  });
}

/** genTextTaskAsyncAiRebuild POST /api/text/gen/async/rebuild */
export async function genTextTaskAsyncAiRebuildUsingPost(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.genTextTaskAsyncAiRebuildUsingPOSTParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseAiResponse_>('/api/text/gen/async/rebuild', {
    method: 'POST',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** getTextTaskById GET /api/text/get */
export async function getTextTaskByIdUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getTextTaskByIdUsingGETParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseTextTask_>('/api/text/get', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** getTextTaskVOById GET /api/text/get/vo */
export async function getTextTaskVoByIdUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getTextTaskVOByIdUsingGETParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseTextTaskVO_>('/api/text/get/vo', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** listTextTaskByPage POST /api/text/list/page */
export async function listTextTaskByPageUsingPost(
  body: API.TextTaskQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponsePageTextTask_>('/api/text/list/page', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** listMyTextTaskByPage POST /api/text/my/list/page */
export async function listMyTextTaskByPageUsingPost(
  body: API.TextTaskQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponsePageTextTask_>('/api/text/my/list/page', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** updateMyTextTask POST /api/text/my/update */
export async function updateMyTextTaskUsingPost(
  body: API.TextUpdateRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseBoolean_>('/api/text/my/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** updateTextTask POST /api/text/update */
export async function updateTextTaskUsingPost(
  body: API.TextUpdateRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseBoolean_>('/api/text/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
