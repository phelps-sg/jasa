
define(`forloop', `pushdef(`$1', `$2')_forloop(`$1', `$2', `$3', `$4') popdef(`$1')')
define(`_forloop', `$4`'ifelse($1, `$3', , `define(`$1', incr($1)) _forloop(`$1', `$2', `$3', `$4')')')


define(`NEW_FUNCTION', `
gp.fs.FN_SET.func.FN_NUMBER = $1
gp.fs.FN_SET.func.FN_NUMBER.nc = $2
define(`FN_NUMBER', incr(FN_NUMBER))
')

define(`NEW_FUNCTION_SET', `
define(`FN_SET', eval(FN_SET+1))
gp.fs.FN_SET = ec.gp.GPFunctionSet
gp.fs.FN_SET.name = $1
gp.fs.FN_SET.size = $2
gp.fs.FN_SET.info = ec.gp.GPFuncInfo
define(`FN_NUMBER', `0')
')

define(`FN_SET', -1)


