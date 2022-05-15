import styled from 'styled-components';

const CARD_WIDTH = 550;
const CARD_HEIGHT = 180;
const CARD_MARGIN = 15;
const SM_WIDTH = CARD_WIDTH + CARD_MARGIN * 2;
const SM_IMAGE_HEIGHT = 300;

export const AppHeading = styled.div`
  color: #000;
  background-color: #eee;
  text-align: center;
  font-size: 1.5em;
  overflow: auto;
`;

export const ShowcaseWrapper = styled.div`
  display: flex;
  justify-content: center;
`;

export const Showcase = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  flex-wrap: wrap;
  width: 100%;
  color: white;
  max-width: ${SM_WIDTH * 2}px;
`;

export const CCard = styled.article`
  width: ${CARD_WIDTH}px;
  height: ${CARD_HEIGHT}px;
  display: flex;
  overflow: hidden;
  background: #3c3e44;
  border-radius: 10px;
  margin: ${CARD_MARGIN}px;
  box-shadow: rgb(0 0 0 / 10%) 0px 4px 6px -1px, rgb(0 0 0 / 6%) 0px 2px 4px -1px;

  @media (max-width: ${SM_WIDTH}px) {
    flex-direction: column;
    width: 100%;
    height: initial;
  }
`;

export const CCardImgWrapper = styled.div`
  flex: 2;
`;

export const CCardImg = styled.img`
  width: 100%;
  height: 100%;
  margin: 0px;
  object-position: center center;
  object-fit: cover;

  @media (max-width: ${SM_WIDTH}px) {
    height: ${SM_IMAGE_HEIGHT}px;
  }
`;

export const CCardContentWrapper = styled.div`
  flex: 3 1 0%;
  position: relative;
  padding: 15px 20px;
  display: flex;
  color: #eee;
  width: 0;
  flex-direction: column;
  box-sizing: border-box;

  span {
    font-size: 16px;
    line-height: 18px;
    font-weight: 500;
    margin: 4px 0;
  }

  h2 {
    font-size: 22px;
    margin: 0px;
  }

  a {
    font-size: 18px;
    text-decoration: none;
    color: #eee;

    :hover {
      color: #f08d49;
    }
  }

  .section {
    flex: 1 1 0%;
    display: flex;
    flex-direction: column;
    justify-content: center;
  }

  .status {
    display: flex;
    align-items: center;
    text-transform: capitalize;
  }

  .status-icon {
    height: 10px;
    width: 10px;
    margin-right: 10px;
    background-color: #9e9e9e;
    border-radius: 50%;
  }

  .status-icon-dead {
    background-color: #d63d2e;
  }

  .status-icon-alive {
    background-color: #55cc44;
  }

  .text-gray {
    color: #9e9e9e;
  }

  @media (max-width: ${SM_WIDTH}px) {
    width: 100%;
  }
`;

export const PaginationWrapper = styled.div`
  display: flex;
  justify-content: center;
  padding: 20px ${CARD_MARGIN}px;
`;

export const CharacterSearchWrapper = styled.div`
  display: flex;
  justify-content: center;
  padding: 50px ${CARD_MARGIN}px 20px;
`;

export const NotFoundWrapper = styled.div`
  color: white;
  text-align: center;
`;
