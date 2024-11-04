import styled, { css } from 'styled-components'

import { ArtworkImageWallLightSources } from './types'

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
  pointer-events: none;
`

export const StyledArtworkImageWall = styled.li<{
  $lightSources: ArtworkImageWallLightSources
}>`
  position: relative;
  display: flex;
  flex-shrink: 0;
  justify-content: center;
  align-items: center;
  width: 100vw;
  height: 100%;
  scroll-snap-align: start;
  background: rgba(0, 0, 0, 0.3);

  ${({ $lightSources }) => {
    switch ($lightSources) {
      case 'single':
        return css`
          &:before {
            ${lightSource};

            left: 35vw;
          }
        `

      case 'double':
        return css`
          &:before {
            ${lightSource};

            left: 15vw;
          }

          &:after {
            ${lightSource};

            right: 15vw;
          }
        `
    }
  }}
`
