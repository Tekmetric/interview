export const humanizeText = (
  text: string,
  titleize: boolean = true
): string => {
  const words = text.split('_')

  if (titleize) {
    return words
      .map(word => word.charAt(0).toUpperCase() + word.slice(1))
      .join(' ')
  }

  return words.join(' ')
}
