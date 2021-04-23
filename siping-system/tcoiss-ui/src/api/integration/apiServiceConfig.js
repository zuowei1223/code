import request from '@/utils/request'

// 查询API服务配置列表
export function listApiServiceConfig(query) {
  return request({
    url: '/webservice/apiServiceConfig/list',
    method: 'get',
    params: query
  })
}

export function getApis(query) {
  return request({
    url: '/webservice/apiServiceConfig/getApis',
    method: 'get',
    params: query
  })
}

// 查询API服务配置详细
export function getApiServiceConfig(id) {
  return request({
    url: '/webservice/apiServiceConfig/' + id,
    method: 'get'
  })
}

// 新增API服务配置
export function addApiServiceConfig(data) {
  return request({
    url: '/webservice/apiServiceConfig',
    method: 'post',
    data: data
  })
}

// 修改API服务配置
export function updateApiServiceConfig(data) {
  return request({
    url: '/webservice/apiServiceConfig',
    method: 'put',
    data: data
  })
}

// 删除API服务配置
export function delApiServiceConfig(id) {
  return request({
    url: '/webservice/apiServiceConfig/' + id,
    method: 'delete'
  })
}
//测试API配置
export function testApiServiceConfig(id) {
  return request({
    url: '/webservice/apiServiceConfig/apiTest' + id,
    method: 'get',
  })
}

