// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** czPoint POST /api/point/cz/user */
export async function czPointUsingPost(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.czPointUsingPOSTParams,
  options?: { [key: string]: any },
) {
  return request<boolean>('/api/point/cz/user', {
    method: 'POST',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** getDailyLoginPoint GET /api/point/get/daily */
export async function getDailyLoginPointUsingGet(options?: { [key: string]: any }) {
  return request<API.BaseResponse>('/api/point/get/daily', {
    method: 'GET',
    ...(options || {}),
  });
}

/** getUserPoint GET /api/point/get/user */
export async function getUserPointUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getUserPointUsingGETParams,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponsePoints_>('/api/point/get/user', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** getPointChangeRecords POST /api/point/log */
export async function getPointChangeRecordsUsingPost(
  body: API.PageRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponsePagePointChanges_>('/api/point/log', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
