import request from '@/utils/request'

// 查询围栏坐标列表
export function listFencePoints(query) {
  return request({
    url: '/webservice/gaode/queryFencePoints',
    method: 'get',
    params: query
  })
}



// 查询围栏坐标详细
export function fencePointsCache(data) {
  return request({
    url: '/webservice/gaode/fencePointsCache',
    method: 'post',
    data: data
  })
}

// 查询围栏坐标详细
export function getFence(FenceId) {
  return request({
    url: '/webservice/fence/' + FenceId,
    method: 'get'
  })
}

// 查询围栏坐标详细
export function addFencePoints() {
  return request({
    url: '/webservice/gaode/addFencePoints',
    method: 'get',
  })
}

// 修改围栏坐标
export function updateFencePoints(data) {
  return request({
    url: '/webservice/gaode/editFencePoints',
    method: 'put',
    data: data
  })
}

// 删除围栏坐标
export function delFencePoints(gid) {
  return request({
    url: '/webservice/gaode/' + gid,
    method: 'delete'
  })
}
//根据地址查询围栏信息
export function queryByAddr(location) {
  return request({
    url: '/webservice/gaode/queryByAddr',
    method: 'get',
    params: {'location':location}
  })
}
export function districtCache(data) {
  return request({
    url: '/webservice/gaode/districtCache',
    method: 'post',
    data: data
  })
}

export function deleteCache() {
  return request({
    url: '/webservice/gaode/deleteCache',
    method: 'delete'
  })
}

export function listCache() {
  return request({
    url: '/webservice/gaode/listCache',
    method: 'get'
  })
}

