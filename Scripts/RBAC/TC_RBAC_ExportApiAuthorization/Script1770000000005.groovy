import utils.ApiClient as ApiClient

def anonymousClient = new ApiClient()
assert anonymousClient.get('/api/export/csv').statusCode == 401 : 'Unauthenticated export requests must return 401.'

def viewerClient = new ApiClient()
viewerClient.signInAsViewer()
assert viewerClient.get('/api/export/csv').statusCode == 403 : 'Viewer export requests must return 403.'

def managerClient = new ApiClient()
managerClient.signInAsManager()
def managerExport = managerClient.get('/api/export/csv')
assert managerExport.statusCode == 200 : 'Manager export requests must return 200.'
assert managerExport.header('Content-Type')?.contains('text/csv') : 'Manager export response must be CSV.'
