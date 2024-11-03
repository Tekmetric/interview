import styled from 'styled-components'

export const StyledGuideTextWrapper = styled.div`
  position: absolute;
  z-index: 2;
  top: 0;
  left: 0;
  right: 0;
  padding: 0 80px;
  box-sizing: border-box;
  min-height: 70px;
  backdrop-filter: blur(10px);
  display: flex;
  justify-content: center;
  align-items: center;
`

export const StyledGuideText = styled.h1`
  font-style: italic;
  font-size: 1rem;
  font-weight: 500;
`
