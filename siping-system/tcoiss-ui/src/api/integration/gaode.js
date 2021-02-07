import request from '@/utils/request'

// 查询电子围栏列表
export function listFence(query) {
  return request({
    url: '/webservice/fence/list',
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

// 新增电子围栏
export function addFence(data) {
  var url = "http://localhost:8088/tcoiss-schedule-admin/gaode/index?";
  url = url+"localKey="+data.localKey+"&name="+data.fenceName;
  window.open(url);
  /*return request({
    url: '/webservice/fence/gaode/add',
    method: 'post',
    data: data
  })*/
}

// 修改电子围栏
export function updateFence(data) {
  return request({
    url: '/webservice/fence',
    method: 'put',
    data: data
  })
}

// 删除电子围栏
export function delFence(id) {
  return request({
    url: '/webservice/fence/' + id,
    method: 'delete'
  })
}
//根据地址查询围栏信息
export function queryByAddr(location) {
  return request({
    url: '/webservice/fence/queryByAddr',
    method: 'get',
    params: {'location':location}
  })
}

