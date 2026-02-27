module.exports = {
    default: [
        '--require-module ts-node/register',
        '--require "Steps/**/*.ts"',
        '--require "hooks.ts"',
        'tests/**/*.feature'
    ].join(' ')
}