import { StyledGuideText, StyledGuideTextWrapper } from './styled'

export const GuideText = () => {
  return (
    <StyledGuideTextWrapper>
      <StyledGuideText>
        Navigate using the arrow keys, touchpad, or horizontal scroll bar{' '}
        {`==>`}
      </StyledGuideText>
    </StyledGuideTextWrapper>
  )
}
