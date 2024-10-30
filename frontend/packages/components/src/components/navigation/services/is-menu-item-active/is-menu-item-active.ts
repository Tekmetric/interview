export const isMenuItemActive = (pathname: string, value: string): boolean => {
  if (!pathname || !value) {
    return false
  }

  const pathParts = pathname.split('?')
  const path = pathParts[0]

  return path.toLowerCase() === value.toLowerCase()
}
