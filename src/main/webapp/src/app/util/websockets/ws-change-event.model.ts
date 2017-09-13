export interface WSChangeEvent<T> {
  type: 'ADDED' | 'UPDATED' | 'DELETED',
  content: T
};
