import styled, { css } from 'styled-components'

const lightSource = css`
  content: '';
  position: absolute;
  top: 0;
  width: 30vw;
  height: 40vh;
  filter: blur(100px);
  background: linear-gradient(#fff 20%, transparent 85%),
    rgba(255, 255, 255, 0.4);
  transform-origin: top center;
  transform: perspective(150px) rotateX(45deg) scale(0.25);
  z-index: 0;
  mix-blend-mode: luminosity;
  pointer-events: none;
`

export const StyledArtworkImageWall = styled.li`
  position: relative;
  display: flex;
  flex-shrink: 0;
  justify-content: center;
  align-items: center;
  width: 100vw;
  height: 100%;
  scroll-snap-align: start;
  background: rgba(0, 0, 0, 0.3);

  &:before {
    ${lightSource};

    left: 15vw;
  }

  &:after {
    ${lightSource};

    right: 15vw;
  }
`
