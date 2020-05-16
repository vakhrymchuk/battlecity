class GameClient {
    constructor(url) {
        this.url = url;
        this.path = url.replace("http", "ws").replace("board/player/", "ws?user=").replace("?code=", "&code=");
    }

    run(callback) {
        this.callback = callback;
        let _ = this
        this.socket = new WebSocket(this.path);
        this.socket.onopen = this.onOpen;
        this.socket.onerror = this.onError;
        this.socket.onclose = this.onClose;
        this.socket.onmessage = (event) => {
            this._message = JSON.parse(event.data.substring(6))
            callback();
        }
    }

    onOpen() {
        text.value += "Connection established\n";
    }

    onClose(event) {
        if (event.wasClean) {
            text.value += "### disconnected ###\n"
        } else {
            text.value += "### accidentally disconnected ###\n";
            text.value += " - Err code: " + event.code + ", Reason: " + event.reason + "\n";
        }
        text.value += "Waiting to reconnect....\n";
        setTimeout(function(){
            text.value += "Connecting....\n";
            location.reload();
        }, 5000);
    }

    onError(error) {
        text.value += "### error ###\n" + error.message + "\n";
    }

    send(msg) {
        text.value += "Sending: " + (msg?msg:'STOP') + '\n'
        this.socket.send(msg)
    }

    printAction(direction, fireOrder) {
        if (direction === undefined) {
            if (fireOrder === undefined) {
                return "STOP";
            }
            switch (fireOrder) {
                case FireOrder.FIRE_AFTER_MOVE:
                    return "ACT(1)";
                case FireOrder.FIRE_BEFORE_MOVE:
                    return "ACT";
            }
        }
        if (fireOrder === undefined) {
            return direction;
        }
        switch (fireOrder) {
            case FireOrder.FIRE_AFTER_MOVE:
                return direction + ",ACT";
            case FireOrder.FIRE_BEFORE_MOVE:
                return "ACT," + direction;
        }
    }
}

const Direction = {
   UP: 'UP',
   LEFT: 'LEFT',
   RIGHT: 'RIGHT',
   DOWN: 'DOWN'
};

const FireOrder = {
   FIRE_BEFORE_MOVE: 'FIRE_BEFORE_MOVE',
   FIRE_AFTER_MOVE: 'FIRE_AFTER_MOVE'
};

Object.freeze(Direction);
Object.freeze(FireOrder);

class Point {
    constructor(x,y){
        this._x = x;
        this._y = y;
    }
    
    equals(o) {
        return o.x() == this._x && o.y() == this._y;
    }

    toString() {
        return '[' + this._x + ',' + this._y + ']';
    }

    isOutOf(boardSize) {
        return this._x >= boardSize || this._y >= boardSize || this._x < 0 || this._y < 0;
    }

    get x() {
        return this._x;
    }

    get y() {
        return this._y;
    }

    diff(o) {
        return new Point(this._x - o._x, this._y - o._y);
    }

    absSum() {
        return Math.abs(this._x) + Math.abs(this._y);
    }

    manhattanDistance(o) {
        return this.diff(o).absSum();
    }
};