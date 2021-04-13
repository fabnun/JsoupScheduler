/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fabnun.jsoupscheduler;

import java.io.Serializable;

/**
 *
 * @author fabian
 */
class TabModel implements Comparable<TabModel>, Serializable {

        public static final long serialVersionUID = 42L;
        
        public String key;
        public String text;
        public int pos;
        public int color;
        public boolean hidden = false;

        public TabModel(String key, String text, int pos, int color, boolean hidden) {
            this.text = text;
            this.pos = pos;
            this.color = color;
            this.hidden = hidden;
            this.key=key;
        }

        public TabModel(String key, String text, int pos, int color) {
            this(key, text, pos, color, false);
        }

        public TabModel(String key, String text, int pos) {
            this(key, text, pos, 0, false);
        }

        public TabModel(String key, String text) {
            this(key, text, -1, 0, false);
        }

        @Override
        public int compareTo(TabModel t) {
            return pos - t.pos;
        }

    @Override
    public String toString() {
        return key+":"+pos;
    }

        
        
    }