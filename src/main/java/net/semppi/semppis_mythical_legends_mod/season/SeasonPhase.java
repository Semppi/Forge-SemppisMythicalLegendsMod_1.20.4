package net.semppi.semppis_mythical_legends_mod.season;

public enum SeasonPhase {

    EARLY_SPRING(0xBC9B6D),
    SPRING(0xD3C600),
    LATE_SPRING(0xAACE42),

    EARLY_SUMMER(0x7CCE00),
    SUMMER(0x5EFF1C),
    LATE_SUMMER(0x59934E),

    EARLY_AUTUMN(0x717000),
    AUTUMN(0xD36100),
    LATE_AUTUMN(0xAE7074),

    EARLY_WINTER(0xA88BC6),
    WINTER(0x4E5DCE),
    LATE_WINTER(0x78A3A3);

    private final int color;

    SeasonPhase(int color) {
        this.color = color;
    }

    public int getColor() {
        return this.color;
    }
}