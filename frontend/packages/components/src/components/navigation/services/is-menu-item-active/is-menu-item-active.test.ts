import { isMenuItemActive } from './is-menu-item-active'

describe('isMenuItemActive', () => {
  it.each([
    ['', 'value', false],
    ['/path', '', false],
    ['/path', '/different', false],
    ['/path', '/path', true],
    ['/path/test', '/path', false],
    ['/Path', '/path', true],
    ['/path?query=param', '/path', true],
    ['/path?query=param', '/different', false]
  ])(
    'returns %s when pathname is %s and value is %s',
    (pathname, value, expected) => {
      expect(isMenuItemActive(pathname, value)).toBe(expected)
    }
  )
})
