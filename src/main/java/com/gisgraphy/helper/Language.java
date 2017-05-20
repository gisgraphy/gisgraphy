package com.gisgraphy.helper;

import com.gisgraphy.addressparser.StreetTypeOrder;

public enum Language {

    /**
     * English
     */
    EN {
	@Override
	public StreetTypeOrder getStreetTypeOrder() {
	    return StreetTypeOrder.nameThenType;
	}
    },
    /**
     * French
     */
    FR {
	@Override
	public StreetTypeOrder getStreetTypeOrder() {
	    return StreetTypeOrder.typeThenName;
	}
    },
    /**
     * Spain
     */
    ES {
	@Override
	public StreetTypeOrder getStreetTypeOrder() {
	    return StreetTypeOrder.typeThenName;
	}
    },
    /**
     * Portuguese
     */
    PT {
	@Override
	public StreetTypeOrder getStreetTypeOrder() {
	    return StreetTypeOrder.typeThenName;
	}
    },
    /**
     * Flemish
     */
    FLEMISH {
	@Override
	public StreetTypeOrder getStreetTypeOrder() {
	    return StreetTypeOrder.nameThenType;
	}
    },
    /**
     * German
     */
    DE {
	@Override
	public StreetTypeOrder getStreetTypeOrder() {
	    return StreetTypeOrder.nameThenType;
	}
    },
    /**
     * En And FR for canada
     */
    EN_FR {
	@Override
	public StreetTypeOrder getStreetTypeOrder() {
	    return StreetTypeOrder.unknow;
	}
    },
    /**
     * Italian
     */
    IT {
	@Override
	public StreetTypeOrder getStreetTypeOrder() {
	    return StreetTypeOrder.typeThenName;
	}
    },
    /**
     * Estonian
     */
    ET {
	@Override
	public StreetTypeOrder getStreetTypeOrder() {
	    return StreetTypeOrder.nameThenType;// need to be verified
	}
    },
    /**
     * english in australia
     */
    EN_AU {
	@Override
	public StreetTypeOrder getStreetTypeOrder() {
	    return StreetTypeOrder.unknow;
	}
    },
    /**
     * Polish
     */
    PL {
	@Override
	public StreetTypeOrder getStreetTypeOrder() {
	    return StreetTypeOrder.typeThenName;
	}
    },
    /**
     * russian
     */
    RU {
	@Override
	public StreetTypeOrder getStreetTypeOrder() {
	    return StreetTypeOrder.nameThenType;
	}
    },
    /**
     * Norwegian
     */
    NO {
	@Override
	public StreetTypeOrder getStreetTypeOrder() {
	    return StreetTypeOrder.unknow;// no streettype
	}
    },

    /**
     * Netherland
     */
    NL {
	@Override
	public StreetTypeOrder getStreetTypeOrder() {
	    return StreetTypeOrder.nameThenType;// included in name
	}
    },
    /**
     * Turk
     */
    TR {
	@Override
	public StreetTypeOrder getStreetTypeOrder() {
	    return StreetTypeOrder.nameThenType;
	}
    },
    /**
     * Indonesian
     */
    ID {
	@Override
	public StreetTypeOrder getStreetTypeOrder() {
	    return StreetTypeOrder.typeThenName;
	}
    },
    /**
     * Finnish
     */
    FI {
	@Override
	public StreetTypeOrder getStreetTypeOrder() {
	    return StreetTypeOrder.unknow;// no street type
	}
    },
    /**
     * Chinese
     */
    CN {
	@Override
	public StreetTypeOrder getStreetTypeOrder() {
	    return StreetTypeOrder.nameThenType;
	}
    },
    /**
     * hungarian
     */
    HU {
	@Override
	public StreetTypeOrder getStreetTypeOrder() {
	    return StreetTypeOrder.nameThenType;
	}
    },
    /**
     * Romanian
     */
    RO {
	@Override
	public StreetTypeOrder getStreetTypeOrder() {
	    return StreetTypeOrder.typeThenName;
	}
    },

    /**
     * Arabic
     */
    AR {
	@Override
	public StreetTypeOrder getStreetTypeOrder() {
	    return StreetTypeOrder.nameThenType;
	}
    },
    /**
     * Swedish
     */
    SE {
	@Override
	public StreetTypeOrder getStreetTypeOrder() {
	    return StreetTypeOrder.nameThenType;
	}
    },
    /**
     * Japanese
     */
    JP {
	@Override
	public StreetTypeOrder getStreetTypeOrder() {
	    return StreetTypeOrder.unknow;
	}
    },
    /**
     * Used for test unsupported language
     */
    UNSUPPORTED {
	@Override
	public StreetTypeOrder getStreetTypeOrder() {
	    return StreetTypeOrder.unknow;
	}
    };

    public abstract StreetTypeOrder getStreetTypeOrder();

}
