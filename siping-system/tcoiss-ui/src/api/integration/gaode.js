import request from '@/utils/request'

// 查询围栏坐标列表
export function listFence(query) {
  return request({
    url: '/webservice/gaode/queryFence',
    method: 'get',
    params: query
  })
}

// 查询围栏坐标列表
export function checkFence(query) {
  return request({
    url: '/webservice/gaode/checkFence',
    method: 'get',
    params: query
  })
}

// 保存缓存
export function saveCache(data) {
  return request({
    url: '/webservice/gaode/saveCache',
    method: 'post',
    data:data
  })
}

export function updateFence(data) {
  return request({
    url: '/webservice/gaode',
    method: 'put',
    data:data
  })
}


// 删除围栏坐标
export function delFence(data) {
  return request({
    url: '/webservice/gaode/delFence',
    method: 'post',
    data:data
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

export function getDistrictByCity(query) {
  return request({
    url: '/webservice/gaode/getDistrictByCity',
    method: 'get',
    params: query
  })
}

// 查询电子围栏详细
export function getFence(id) {
  return request({
    url: '/webservice/fence/' + id,
    method: 'get'
  })
}




