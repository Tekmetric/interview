import { renderHook, act } from '@testing-library/react-native';
import { useFilterModal } from '../useFilterModal';

describe('useFilterModal', () => {
  it('should initialize with showFilterModal as false', () => {
    const { result } = renderHook(() => useFilterModal());
    expect(result.current.showFilterModal).toBe(false);
  });

  it('should toggle showFilterModal when toggleFilterModal is called', () => {
    const { result } = renderHook(() => useFilterModal());

    act(() => {
      result.current.toggleFilterModal();
    });

    expect(result.current.showFilterModal).toBe(true);

    act(() => {
      result.current.toggleFilterModal();
    });

    expect(result.current.showFilterModal).toBe(false);
  });

  it('should set showFilterModal to false when closeFilterModal is called', () => {
    const { result } = renderHook(() => useFilterModal());

    act(() => {
      result.current.toggleFilterModal();
    });

    act(() => {
      result.current.closeFilterModal();
    });

    expect(result.current.showFilterModal).toBe(false);
  });
});
