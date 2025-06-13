import styled from 'styled-components';

const Container = styled.div`
	display: flex;
	flex-direction: column;
	width: 100%;
	height: 100%;
	overflow: hidden;
	border-radius: ${({ theme }) => theme.spacing03};
	border: 1px solid ${({ theme }) => theme.borderColor};
`;

const ScrollableContent = styled.div`
	overflow: auto;
	height: 100%;
`;

const HeaderRow = styled.div<{ template: string }>`
	display: grid;
	grid-template-columns: ${({ template }) => template};
	position: sticky;
	top: 0;
	width: 100%;
	z-index: 1;
`;

const HeaderCell = styled.div`
	display: flex;
	align-items: center;
	user-select: none;
	width: 100%;
	height: auto;
	overflow: hidden;
	background-color: ${({ theme }) => theme.ui02};
	color: ${({ theme }) => theme.textColor02};
	padding: ${({ theme }) => theme.spacing04};
`;

const HeaderActiveCell = styled.button`
	all: unset;
	box-sizing: border-box;
	cursor: pointer;
	display: flex;
	align-items: center;
	user-select: none;
	width: 100%;
	height: auto;
	overflow: hidden;
	background-color: ${({ theme }) => theme.ui02};
	color: ${({ theme }) => theme.textColor02};
	padding: ${({ theme }) => theme.spacing04};
`;

const Checkbox = styled.input`
	cursor: pointer;

	&:focus {
		outline-color: ${({ theme }) => theme.focusColor};
	}
`;

const Cell = styled.div<{ height: number }>`
	display: flex;
	width: 100%;
	height: ${({ height }) => height}px;
	overflow: hidden;
	padding: ${({ theme }) => theme.spacing03} ${({ theme }) => theme.spacing04};
	border-bottom: 1px solid ${({ theme }) => theme.borderColor};
`;

const Row = styled.div<{ template: string }>`
	display: grid;
	grid-template-columns: ${({ template }) => template};

	& > * {
		background-color: ${({ theme }) => theme.ui01};
		color: ${({ theme }) => theme.textColor01};
	}

	&:hover > * {
		background-color: ${({ theme }) => theme.ui01Hover};
	}
`;

const CustomCell = styled.div<{ height?: number }>`
	display: flex;
	width: 100%;
	height: ${({ height }) => (height ? `${height}px` : '100%')};
	position: sticky;
	left: 0;
`;

const NoDataDisplayContainer = styled.div`
	display: flex;
	justify-content: center;
	align-items: center;
	width: 100%;
	height: 100%;
`;

export {
	Container,
	ScrollableContent,
	HeaderRow,
	HeaderCell,
	HeaderActiveCell,
	Checkbox,
	Row,
	Cell,
	NoDataDisplayContainer,
	CustomCell,
};
