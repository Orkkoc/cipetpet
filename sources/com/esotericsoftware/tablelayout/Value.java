package com.esotericsoftware.tablelayout;

public abstract class Value {
    public static Value maxHeight = new CellValue() {
        public float get(Cell cell) {
            if (cell == null) {
                throw new RuntimeException("maxHeight can only be set on a cell property.");
            }
            Object widget = cell.widget;
            if (widget == null) {
                return 0.0f;
            }
            return Toolkit.instance.getMaxHeight(widget);
        }
    };
    public static Value maxWidth = new CellValue() {
        public float get(Cell cell) {
            if (cell == null) {
                throw new RuntimeException("maxWidth can only be set on a cell property.");
            }
            Object widget = cell.widget;
            if (widget == null) {
                return 0.0f;
            }
            return Toolkit.instance.getMaxWidth(widget);
        }
    };
    public static Value minHeight = new CellValue() {
        public float get(Cell cell) {
            if (cell == null) {
                throw new RuntimeException("minHeight can only be set on a cell property.");
            }
            Object widget = cell.widget;
            if (widget == null) {
                return 0.0f;
            }
            return Toolkit.instance.getMinHeight(widget);
        }
    };
    public static Value minWidth = new CellValue() {
        public float get(Cell cell) {
            if (cell == null) {
                throw new RuntimeException("minWidth can only be set on a cell property.");
            }
            Object widget = cell.widget;
            if (widget == null) {
                return 0.0f;
            }
            return Toolkit.instance.getMinWidth(widget);
        }
    };
    public static Value prefHeight = new CellValue() {
        public float get(Cell cell) {
            if (cell == null) {
                throw new RuntimeException("prefHeight can only be set on a cell property.");
            }
            Object widget = cell.widget;
            if (widget == null) {
                return 0.0f;
            }
            return Toolkit.instance.getPrefHeight(widget);
        }
    };
    public static Value prefWidth = new CellValue() {
        public float get(Cell cell) {
            if (cell == null) {
                throw new RuntimeException("prefWidth can only be set on a cell property.");
            }
            Object widget = cell.widget;
            if (widget == null) {
                return 0.0f;
            }
            return Toolkit.instance.getPrefWidth(widget);
        }
    };
    public static final Value zero = new CellValue() {
        public float get(Cell cell) {
            return 0.0f;
        }

        public float get(Object table) {
            return 0.0f;
        }
    };

    public abstract float get(Cell cell);

    public abstract float get(Object obj);

    public float width(Object table) {
        return Toolkit.instance.width(get(table));
    }

    public float height(Object table) {
        return Toolkit.instance.height(get(table));
    }

    public float width(Cell cell) {
        return Toolkit.instance.width(get(cell));
    }

    public float height(Cell cell) {
        return Toolkit.instance.height(get(cell));
    }

    public static abstract class CellValue extends Value {
        public float get(Object table) {
            throw new UnsupportedOperationException("This value can only be used for a cell property.");
        }
    }

    public static abstract class TableValue extends Value {
        public float get(Cell cell) {
            return get(cell.getLayout().getTable());
        }
    }

    public static class FixedValue extends Value {
        private float value;

        public FixedValue(float value2) {
            this.value = value2;
        }

        public void set(float value2) {
            this.value = value2;
        }

        public float get(Object table) {
            return this.value;
        }

        public float get(Cell cell) {
            return this.value;
        }
    }

    public static Value percentWidth(final float percent) {
        return new TableValue() {
            public float get(Object table) {
                return Toolkit.instance.getWidth(table) * percent;
            }
        };
    }

    public static Value percentHeight(final float percent) {
        return new TableValue() {
            public float get(Object table) {
                return Toolkit.instance.getHeight(table) * percent;
            }
        };
    }

    public static Value percentWidth(final float percent, final Object widget) {
        return new Value() {
            public float get(Cell cell) {
                return Toolkit.instance.getWidth(widget) * percent;
            }

            public float get(Object table) {
                return Toolkit.instance.getWidth(widget) * percent;
            }
        };
    }

    public static Value percentHeight(final float percent, final Object widget) {
        return new TableValue() {
            public float get(Object table) {
                return Toolkit.instance.getHeight(widget) * percent;
            }
        };
    }
}
