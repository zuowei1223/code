import request from '@/utils/request'

// 查询代码生成业务列表
export function listTable(query) {
  return request({
    url: '/datafactory/table/list',
    method: 'get',
    params: query
  })
}

// 查询代码生成业务详细
export function getBusTable(tableId) {
  return request({
    url: '/datafactory/table/' + tableId,
    method: 'get'
  })
}

// 新增代码生成业务
export function addTable(data) {
  return request({
    url: '/datafactory/table',
    method: 'post',
    data: data
  })
}

// 修改代码生成业务
export function updateBusTable(data) {
  return request({
    url: '/datafactory/table/updateBusTable',
    method: 'put',
    data: data
  })
}

export function updateTable(data) {
  return request({
    url: '/datafactory/table',
    method: 'put',
    data: data
  })
}

// 删除代码生成业务
export function delTable(tableId) {
  return request({
    url: '/datafactory/table/' + tableId,
    method: 'delete'
  })
}

export function syncTableJg(data) {
  return request({
    url: '/datafactory/table/syncTableJg',
    method: 'post',
    data: data
  })
}
export function initTableData(data) {
  return request({
    url: '/datafactory/table/initTableData',
    method: 'post',
    data: data
  })

}

