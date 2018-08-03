

#curl -X GET --header 'Accept: application/json' 'http://149.165.169.127:8080/biomine/indexer/index/status'

function post {
    echo -n "Posting $1 : " 
    curl -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' "http://149.165.169.127:8080/biomine/indexer/index/path/{doc}?path=%2Fvar%2Fdata%2Fpubmed%2Fbaseline%2F$1&collection=literature"
    echo
}

function part1 {
    for i in `seq 1 9` ; do
	post pubmed18n000$i.xml
    done
}

function part2 {
    for i in `seq 10 99` ; do
	post pubmed18n00$i.xml
    done
}

function part3 {
    for i in `seq 100 928` ; do
	post pubmed18n0$i.xml
    done
}

while [[ -n "$1" ]] ; do
    case $1 in
	 1) part1 ;;
	 2) part2 ;;
	 3) part3 ;;
	 all)
	     part1
	     part2
	     part3
	     ;;
	 id)
	     post pubmed18n0$2.xml
	     shift
	     ;;
	 *)
	     echo "Invalid option $1"
	     exit 1
	     ;;
    esac
    shift
done
