# SimpleLibrary
It's a simple library that makes programming in android faster

## SimpleRecyclerAdapter
It's used to create RecyclerView Adapters faster with less code. 
These Adapters come with an ArrayList and an automatic management system for it.

Pre programmed methods like:
```
add(V value);
add(int index, V value);
remove(V value);
remove(int index);
move(int from, int to);

getContext();
getRecyclerView();
smoothScrollToPosition(int pos);
smoothScrollToPosition(V value);
...
```

Usage:
```
public class TestAdapter extends SimpleRecyclerAdapter<String> {
    @Override
    public View createHolder(ViewGroup parent, int viewType) {
        //Create item layout here
        return inflateLayout(parent, R.layout.test_item);
    }

    @Override
    public void bindHolder(ViewHolder holder, String value, int pos) {
        //Find and bind your views here
        
        //Like:
        //TextView textView = (TextView) holder.findViewById(R.id.test_item_text);
    }
}
```

## SimpleRecyclerFilterAdapter
It's the same as the above SimpleRecyclerAdapter, but you can apply a filter to it.
That means that items can get enabled / disabled by the filter. It also updates dynamically (with animation).

Usage:
```
public class TestAdapter extends SimpleRecyclerFilterAdapter<String> {
    @Override
    public View createHolder(ViewGroup parent, int viewType) {
        //Create item layout here
        return inflateLayout(parent, R.layout.test_item);
    }

    @Override
    public void bindHolder(ViewHolder holder, String value, int pos) {
        //Find and bind your views here
        
        //Like:
        //TextView textView = (TextView) holder.findViewById(R.id.test_item_text);
    }
    
    @Override
    public SimpleFilter<String> applyFilter() {
        return new SimpleFilter<String>() {
            @Override
            public boolean filter(String value) {
                //Enable / disable list item by returning true or false
                return false;
            }
        };
    }
}
```
You don't need to override the applyFilter methode. You can also set the filter with "setFilter(SimpleFilter filter)" at any time.

## SimpleItemTouchHelper
If you are using SimpleRecyclerAdapter or SimpleRecyclerFilterAdapter you can add a SimpleItemTouchHelper to your RecyclerView.
That allows you to make your list items swipeable or dragable (on longclick).

Usage:
```
SimpleItemTouchHelper.apply(recyclerView);
```
or if you want to specify it more
```
new SimpleItemTouchHelper(recyclerView) {
            @Override
            public void onSwipeItem(int position, int direction) {
                
            }

            @Override
            public void onRemoveItem(int position, int direction) {
                
            }

            @Override
            public void onMoveItem(int fromPos, int toPos) {
                
            }
        };
```

You can also change the behavior of the TouchHelper by using the following methods on it
```
simpleItemTouchHelper.getSettings().setItemViewSwipeEnabled(true);
simpleItemTouchHelper.getSettings().setDeleteItemOnSwipe(true);
simpleItemTouchHelper.getSettings().setLongPressDragEnabled(true);
```
or set the swipe and drag flags
```
simpleItemTouchHelper.getSettings().setSwipeFlags(SimpleSettings.SWIPE_START_END);
simpleItemTouchHelper.getSettings().setDragFlags(SimpleSettings.DRAG);
```

## More
* Tools
* Math tools
* Animation tools
* Image tools
* Color tools
* Point tools
* Vectors
* Time tools
* Simple popup menus

## Installation
Add this to your build.gradle:
```
allprojects {
    repositories {
        jcenter()
        maven { url 'https://jitpack.io' }
    }
}
```

Add the dependency:
```
implementation 'com.github.ProDev2:SimpleLibrary:6.0'
```

## Details
#License Copyright (C) by Pascal Gerner
```
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
